package model

import java.util.Date

import common.PortalDateFormatter
import controllers.routes
import uk.gov.dvla.time.TimeUtils
import uk.gov.dvla.domain.{TaxStatus, Vehicle}
import utils.StringExtentions._
import utils.IntExtensions._
import utils.URLBuilderUtils

/**
 * View Model for the single vehicle view.
 *
 * The view model is designed to be used directly in the view to represent the vehicle.
 * It should only contain the fields that are displayed in the associated view.
 * It encapsulates any display logic, e.g. formatting, so the fields should all be
 * Option[String].
 *
 * The view should be able to simple use
 *   if (vm.xxxx.isDefined) ...  to tell if the field is available
 * and
 *   vm.xxxx                     to display the formatted field value
 *
 */
class VehicleDetailsViewModel(val registrationNumber: Option[String],
                              val make: Option[String],
                              val liability: Option[String],
                              val firstRegistration: Option[String],
                              val fleetNumber: Option[String],
                              val taxClass: Option[String],
                              val taxStatus: Option[String],
                              val cylinderCapacity: Option[Int],
                              val co2Emissions: Option[Int],
                              val taxBand: Option[String],
                              val fuelType: Option[String],
                              val colour: Option[String],
                              val massInService: Option[String],
                              val numberOfSeats: Option[String],
                              val standingCapacity: Option[String],
                              val revenueWeight: Option[String],
                              val wheelplan: Option[String],
                              val firstRegistrationUk: Option[String],
                              val vehicleCategory: Option[String],
                              val bodyType: Option[String],
                              val isSORN: Boolean,
                              val isMotCurrent: Boolean,
                              val motExpiry: String,
                              val motMessage: String,
                              val model: Option[String],
                              val vin: Option[String],
                              val lastV5: Option[String],
                              val engineNumber: Option[String]) {

  def backUrl(pageNo:Int, filterSearchData:VehicleRefinedSearchData) =
    URLBuilderUtils.buildUrl(routes.ViewFleetRecords.refinedSearchSubmit(pageNo).url, filterSearchData.toQueryMap)

}

object VehicleDetailsViewModel {

  private val VEHICLE_CATEGORY_TO_DISPLAY_TAXBAND = "M1"

  def apply(vehicle: Vehicle): VehicleDetailsViewModel = {
    new VehicleDetailsViewModel(
      Some(vehicle.registrationNumber),
      formatCapitalizeFull(vehicle.make),
      formatDate(vehicle.liability),
      formatDate(vehicle.firstRegistration),
      Some(vehicle.fleetNumber),
      Some(vehicle.taxCodeWithDescription),
      vehicle.taxStatus,
      ignoreZeros(vehicle.cylinderCapacity),
      ignoreZeros(vehicle.co2Emissions),
      retrieveTaxbandBasedOnVehicleCategory(vehicle.vehicleCategory, vehicle),
      Some(vehicle.fuelType),
      formatCapitalize(vehicle.colour),
      ignoreZeros(vehicle.massInService).map(_.formatUK),
      ignoreZeros(vehicle.numberOfSeats).map(_.formatUK),
      ignoreZeros(vehicle.standingCapacity).map(_.formatUK),
      ignoreZeros(vehicle.revenueWeight).map(_.formatUK),
      Some(vehicle.wheelplan),
      formatDate(vehicle.firstRegistrationUk),
      vehicle.vehicleCategory,
      Some(vehicle.bodyType),
      Vehicle.isSORN(vehicle.licensingType),
      MotStatus.isCurrent(vehicle.motExpiry),
      MotStatus.formatDate(vehicle.motExpiry),
      MotStatus.getMessage(vehicle.motExpiry),
      formatCapitalizeFull(vehicle.model),
      vehicle.vin,
      formatDate(vehicle.lastV5),
      vehicle.engineNumber
    )
  }

  def formatCapitalize(string: String) = {
    Option(string).map(s => s.capitalize)
  }

  def formatCapitalizeFull(string: String) = {
    Option(string).map(s => s.capitalizeFull)
  }

  def formatCapitalizeFull(string: Option[String]) = {
    string.map(s => s.capitalizeFull)
  }

  def formatDate(date: Date) = {
    Option(date).map(s => PortalDateFormatter.format(s))
  }

  def formatDate(dateOpt: Option[Date]) = {
    dateOpt.map(s => PortalDateFormatter.format(s))
  }

  def retrieveTaxbandBasedOnVehicleCategory(vehicleCategory: Option[String], vehicle: Vehicle): Option[String] = {
    if (vehicleCategory == Some(VEHICLE_CATEGORY_TO_DISPLAY_TAXBAND))
      vehicle.taxBand
    else
      None
  }

  /**
   * Ensures that zero's are ignored
   * @param i value to be checked
   * @return None if empty or value is 0
   */
  def ignoreZeros(i: Option[Int]): Option[Int] = {
    if (i.nonEmpty && i.get != 0) i else None
  }

  def isTaxStatusOK(vehicle: VehicleDetailsViewModel): Boolean = {
    vehicle.taxStatus == Some(TaxStatus.TAXED) || vehicle.taxStatus == Some(TaxStatus.SORN)
  }
}

object MotStatus {
  val CURRENT = "current"
  val EXPIRED = "expired"
  val UNKNOWN = "unknown"

  def isCurrent(motExpiry: Option[Date]) = getStatus(motExpiry) == MotStatus.CURRENT

  def getMessage(motExpiry: Option[Date]) = {
    getStatus(motExpiry) match {
      case MotStatus.EXPIRED => "vsv.mot.expired"
      case MotStatus.UNKNOWN => "vsv.mot.unknown"
      case MotStatus.CURRENT => "vsv.mot.expires"
    }
  }

  private def getStatus(motExpiry: Option[Date]): String = {
    // MOT expiry date is not recorded on database
    if (motExpiry.isEmpty) {
      return MotStatus.UNKNOWN
    }

    val startOfToday = TimeUtils.getStartOfToday.getTime
    if (motExpiry.get.before(startOfToday)) {
      // expired MOT, (MOT is in the past)
      MotStatus.EXPIRED
    }
    else {
      // current MOT (MOT is today’s date or in the future)
      MotStatus.CURRENT
    }
  }

  def formatDate(motExpiry: Option[Date]): String = {
    VehicleDetailsViewModel.formatDate(motExpiry).getOrElse("")
  }
}
