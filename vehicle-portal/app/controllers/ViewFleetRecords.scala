package controllers

import org.slf4j.LoggerFactory
import play.api.mvc._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import services.{ReferenceEnquiryService, VehicleFilterService}
import model._
import utils.{URLBuilderUtils, URLEncodingUtils}
import utils.SessionUtils._
import uk.gov.dvla.domain.FleetFilter
import play.api.libs.json.Json
import scala.concurrent._
import scala.concurrent.duration._
import uk.gov.dvla.domain.FleetFilter.validateFilter
import model.FleetViewModel.validatePageOffset


trait ViewFleetRecords extends Controller {

  private val logger = LoggerFactory.getLogger(getClass)
  val vehicleFilterService: VehicleFilterService
  val referenceEnquiryService: ReferenceEnquiryService

  lazy val makesCache = {
    logger.debug("Initializing vehicle makes cache")
    Await.result(referenceEnquiryService.makes.map(Json.stringify), 3 seconds)
  }

  def selectPageSize(pageSize: Int) = Action {
    implicit request => {
      val params = FleetParams()
      Redirect(viewFleetUrl(1, params.data))
        .withSession(session.withPaging(0, pageSize.min(100).max(0), session.maxOffset))
    }
  }

  def viewFleet(pageNo: Int) = Action.async {
    implicit request => {
      logger.trace("viewFleet entered")
      val params = FleetParams()
      generateViewFleetActionResult(params, session, pageNo)
    }
  }

  def refinedSearchSubmit(pageNo: Int) = Action.async {
    implicit request =>
      logger.debug("testing refinedSearchSubmit "+request.body.toString())
      val params = FleetParams()
      if (params.form.hasErrors)
      {
        logger.debug("refinedSearchSubmit bind error", params.form)
        generateViewFleetActionResult(params, session, pageNo)
      } else {
        logger.debug("refinedSearchSubmit bind success, redirecting to routes.Search.vehicleDetails")
        Future(Redirect(viewFleetUrl(1, params.data)))
      }
  }

  private def generateViewFleetActionResult(params: FleetParams, session: Session, pageNo: Int) = {
    logger.debug("testing generateViewFleetActionResult "+params.form.toString)
    val fleetNumber = session.fleetNumber
    val limit = session.limit
    val offset = validatePageOffset((pageNo - 1) * limit, limit, session.maxOffset)

    val servicesToCall = for {
      header <- vehicleFilterService.getFleetHeaderData(fleetNumber)
      fleet <- vehicleFilterService.getFleetRecords(fleetNumber, offset, limit, params.data)
    } yield (header, fleet)

    servicesToCall.map(
      results => {
        val (header, fleet) = results

        if (header.status == OK && fleet.status == OK) {
          logger.debug("viewFleet header statuses are OK -> getting more data")
          val fleetHeader = FleetHeader(header.json)
          val fleetViewModel = FleetViewModel(fleet.json, offset, limit, fleetNumber, params, makesCache)

          logger.debug("viewFleet data acquired -> views.html.viewFleetRecords")

          Ok(views.html.viewFleetRecords(
            fleetHeader,
            fleetViewModel
          )).withSession(session.withPaging(offset, limit, fleetViewModel.vehiclesFound - 1).withBackToFleetPage(pageNo))
        } else {
          logger.debug("viewFleet header statuses NOT OK -> Redirect(routes.Search.searchForm)")
          Redirect(routes.Search.searchForm).withError("vvrform.the.requested.record.could.not.be.found")
        }
      }
    )
  }

  def redirectToVehicleDetails = Action {
    implicit request => {
      val registrationNumber = request.getQueryString("registrationNumber").getOrElse("")
      val vehicleMake = request.getQueryString("vehicleMake").getOrElse("")
      logger.debug(s"Redirect to vehicle details page with registration number:$registrationNumber, make:$vehicleMake")
      val fleetNumber = session.fleetNumber()
      val params = FleetParams()
      Redirect(vehicleDetailsUrl(params.data)).withSession(
        session.withVehicleData(fleetNumber, registrationNumber, URLEncodingUtils.decode(vehicleMake))
      )
    }
  }

  private def viewFleetUrl(pageNo:Int, searchFilter:VehicleRefinedSearchData) =
    URLBuilderUtils.buildUrl(routes.ViewFleetRecords.viewFleet(pageNo).url, searchFilter.toQueryMap)

  private def vehicleDetailsUrl(searchFilter:VehicleRefinedSearchData) =
    URLBuilderUtils.buildUrl(routes.Search.vehicleDetails.url, searchFilter.toQueryMap)
}

object ViewFleetRecords extends ViewFleetRecords {
  val vehicleFilterService = VehicleFilterService
  val referenceEnquiryService = ReferenceEnquiryService
}

