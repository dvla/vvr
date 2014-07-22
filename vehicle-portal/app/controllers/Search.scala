package controllers

import play.api.mvc._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import services.{ReferenceEnquiryService, VehicleFilterService}
import forms.VehicleSearchForm
import org.slf4j.LoggerFactory
import play.api.libs.json._
import model.{FleetParams, VehicleRefinedSearchData, VehicleDetailsViewModel, VehicleData}
import scala.concurrent._
import scala.concurrent.duration._
import utils.SessionUtils._
import uk.gov.dvla.domain.Vehicle


trait Search extends Controller with VehicleSearchForm {

  private val logger = LoggerFactory.getLogger(getClass)
  val vehicleFilterService: VehicleFilterService
  val referenceEnquiryService: ReferenceEnquiryService

  lazy val makesCache = {
    logger.debug("Initializing vehicle makes cache")
    Await.result(referenceEnquiryService.makes.map(Json.stringify), 3 seconds)
  }

  def searchForm = Action {
    implicit request =>
      Ok(views.html.searchForm(makesCache, vehicleForm, session.errors)).withNewSession
  }

  def searchSubmit = Action {
    implicit request =>
      vehicleForm.bindFromRequest.fold(
        error => {
          logger.debug("searchSubmit bind error", error)
          Ok(views.html.searchForm(makesCache, error))
        },
        success => {
          logger.debug("searchSubmit bind success, redirecting to routes.Search.vehicleDetails")
          logger.debug("searchSubmit bind success, Vehicle Make :"+success.vehicleMake)
          Redirect(routes.Search.vehicleDetails).withSession(
            session.withVehicleData(success).withBackToFleetPage(0)
          )
        }
      )
  }

  def vehicleDetails = Action.async {
    implicit request =>
      val backToFleetPage = session.backToFleetPage
      session.bindToForm(vehicleForm).fold(
        formWithErrors => {
          logger.debug("vehicleDetails bind error -> Redirect(routes.Search.searchForm)", formWithErrors)
          Future(Redirect(routes.Search.searchForm).withNewSession)
        },
        validVehicleData => {
          logger.debug("vehicleDetails bind success")
          val params = FleetParams()
          handleSubmit(validVehicleData, backToFleetPage, params.data)
        }
      )
  }

  private def handleSubmit(vehicleData: VehicleData, backToFleetPage: Int, fleetSearchFilter: VehicleRefinedSearchData) = {
    vehicleFilterService.searchVehicle(vehicleData).map {
      response =>
        response.status match {
          case OK => {
            logger.debug("handleSubmit OK -> views.html.searchResult")
            Ok(views.html.searchResult(VehicleDetailsViewModel(Vehicle(response.json)), backToFleetPage, fleetSearchFilter))
          }
          case NOT_FOUND => {
            logger.debug("handleSubmit NOT_FOUND -> Redirect(routes.Search.searchForm)")
            Redirect(routes.Search.searchForm)
              .withError("vvrform.the.requested.record.could.not.be.found")
          }
          case UNPROCESSABLE_ENTITY => {
            logger.debug("handleSubmit UNPROCESSABLE_ENTITY -> Redirect(routes.Search.searchForm)")
            Redirect(routes.Search.searchForm)
              .withError("vvrform.this.vehicle.is.not.part.of.this.fleet")
          }
        }
    }
  }
}

object Search extends Search {
  val vehicleFilterService = VehicleFilterService
  val referenceEnquiryService = ReferenceEnquiryService
}
