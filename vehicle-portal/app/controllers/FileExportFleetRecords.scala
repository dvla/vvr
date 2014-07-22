package controllers

import play.api.mvc._
import model.{FleetParams, FleetViewModel}
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import play.api.libs.iteratee.Enumerator
import utils.files.{QuoteSurroundFormatter, CSVFileContentBuilder}
import java.nio.charset.StandardCharsets
import java.util.Date
import common.PortalDateFormatter._
import play.api.mvc.ResponseHeader
import utils.SessionUtils._
import play.api.mvc.SimpleResult
import services.VehicleFilterService
import org.slf4j.LoggerFactory
import uk.gov.dvla.time.TimeUtils
import uk.gov.dvla.domain.FleetFilter

trait FileExportFleetRecords extends Controller {

  private val logger = LoggerFactory.getLogger(getClass)

  def timer: TimeUtils
  def vehicleFilterService: VehicleFilterService

  def exportFleet = Action.async {
    implicit request => {
      logger.trace("exportFleet begin")
      val fleetNumber = session.fleetNumber
      val params = FleetParams()

      val servicesToCall = for {
        fleet <- vehicleFilterService.getFleetRecords(fleetNumber, 0, 0, params.data)
      } yield fleet

      logger.trace("exportFleet before servicesToCall.map")
      servicesToCall.map(
        results => {
          val fleet = results
          if (fleet.status == OK) {
            logger.debug("exportFleet fleet status is OK -> creating data")
            val fleetHelper = FleetViewModel(fleet.json)
            val fileContent: Enumerator[Array[Byte]] =
              Enumerator.fromStream(CSVFileContentBuilder.create(FileExportFleetRecords.vehicleToFileMapper, fleetHelper, StandardCharsets.UTF_8))

            logger.debug("data created -> returning SimpleResult")
            SimpleResult(
              header = ResponseHeader(OK,
                Map("Content-Disposition" -> ("attachment; filename=\"" + exportFileName(fleetNumber, timer.currentTime, params.data.filter.getOrElse(FleetFilter.ALL), "csv") + "\""),
                  "Content-Type" -> "text/csv",
                  "Charset" -> "UTF-8")),
              body = fileContent
            )
          } else {
            logger.debug("exportFleet fleet status is NOT OK -> Redirect(routes.Search.searchForm)")
            Redirect(routes.Search.searchForm).withError("vvrform.the.requested.record.could.not.be.found")
          }
        }
      )

    }
  }

  private def exportFileName(fleetNumber: String, currentTime: Date, filter: String, fileExtension: String): String = {
    Seq("fleet", fleetNumber, "export", filter, yyyyMMdd(currentTime), HHmmss(currentTime)).mkString("_") +
      "." + fileExtension
  }

}

object FileExportFleetRecords extends FileExportFleetRecords {

  val vehicleFilterService = VehicleFilterService

  val vehicleToFileMapper = Seq(
    ("registrationNumber", "Registration number", None),
    ("make", "Vehicle make", Some(QuoteSurroundFormatter)),
    ("taxStatus", "Tax status", Some(QuoteSurroundFormatter)),
    ("liability", "Date of liability", None),
    ("motExpiry", "MOT expiry", None),
    ("firstRegistration", "Date of first registration", None),
    ("firstRegistrationUk", "Date of first registration in UK", None),
    ("taxClass", "Tax class", Some(QuoteSurroundFormatter)),
    ("cylinderCapacity", "Cylinder Capacity", None),
    ("co2Emissions", "CO2 emissions", None),
    ("fuelType", "Fuel type", None),
    ("taxBand", "Tax band", None),
    ("colour", "Vehicle colour", None),
    ("bodyType", "Body type", Some(QuoteSurroundFormatter)),
    ("revenueWeight", "Revenue weight", None),
    ("massInService", "Mass in service", None),
    ("numberOfSeats", "Number of seats", None),
    ("standingCapacity", "Standing capacity", None),
    ("wheelplan", "Wheelplan", Some(QuoteSurroundFormatter)),
    ("vehicleCategory", "Vehicle category", None)
  )

  val timer = TimeUtils

}
