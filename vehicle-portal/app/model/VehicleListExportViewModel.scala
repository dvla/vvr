package model

import uk.gov.dvla.domain.{TaxStatus, Vehicle}
import common.PortalDateFormatter
import utils.StringExtentions._

object VehicleListExportViewModel {

  def apply(vehicle: Vehicle): VehicleListExportViewModel = {
    new VehicleListExportViewModel(
      Some(vehicle.registrationNumber),
      Option(vehicle.make).map(s => s.capitalizeFull),
      vehicle.taxStatus,
      getLiabilityForMultiViewExport(vehicle),
      Option(vehicle.firstRegistration).map(PortalDateFormatter.shortDateFormat),
      vehicle.firstRegistrationUk.map(PortalDateFormatter.shortDateFormat),
      Some(vehicle.fleetNumber),
      Some(vehicle.taxCodeWithDescription),
      vehicle.cylinderCapacity,
      vehicle.co2Emissions,
      vehicle.taxBand,
      vehicle.fuelType,
      Option(vehicle.colour).map(s => s.capitalize),
      Some(vehicle.bodyType),
      vehicle.revenueWeight,
      vehicle.massInService,
      vehicle.numberOfSeats,
      vehicle.standingCapacity,
      Some(vehicle.wheelplan),
      vehicle.vehicleCategory,
      vehicle.motExpiry.map(PortalDateFormatter.shortDateFormat)
    )
  }

  /**
   * //US1019 Scenario 5
   * @return "N/A" or formatted liability
   */
  def getLiabilityForMultiViewExport(vehicle: Vehicle) = {
    if (vehicle.taxStatus == Some(TaxStatus.SORN)) {
      Some("N/A")
    } else {
      vehicle.liability.map(PortalDateFormatter.shortDateFormat)
    }
  }

  /**
   * //US1019 Scenario 5 (part)
   * @return taxStatus
   */
  def getTaxStateForMultiViewExport(vehicle: Vehicle) = {
    if (vehicle.taxStatus == Some(TaxStatus.SORN)) {
      TaxStatus.SORN
    } else {
      vehicle.taxStatus
    }
  }
}

case class VehicleListExportViewModel(
                                       registrationNumber: Option[String],
                                       make: Option[String],
                                       taxStatus: Option[String],
                                       liability: Option[String],
                                       firstRegistration: Option[String],
                                       firstRegistrationUk: Option[String],
                                       fleetNumber: Option[String],
                                       taxClass: Option[String],
                                       cylinderCapacity: Option[Int],
                                       co2Emissions: Option[Int],
                                       taxBand: Option[String],
                                       fuelType: String,
                                       colour: Option[String],
                                       bodyType: Option[String],
                                       revenueWeight: Option[Int],
                                       massInService: Option[Int],
                                       numberOfSeats: Option[Int],
                                       standingCapacity: Option[Int],
                                       wheelplan: Option[String],
                                       vehicleCategory: Option[String],
                                       motExpiry: Option[String]) {
}
