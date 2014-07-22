package model

import java.util.Date

import common.PortalDateFormatter
import uk.gov.dvla.domain.{TaxStatus, Vehicle}
import utils.StringExtentions._
import utils.URLBuilderUtils
import controllers.routes

object VehicleListViewModel {

  def apply(vehicle: Vehicle): VehicleListViewModel = {
    new VehicleListViewModel(
      Some(vehicle.registrationNumber),
      Option(vehicle.make).map(s => s.capitalizeFull),
      formatLiabilityForMultiView(vehicle),
      Option(PortalDateFormatter.shortDateFormat(vehicle.firstRegistration)),
      Some(vehicle.fleetNumber),
      Some(vehicle.taxCode))
  }

  /**
   * //US1019 Scenario 4
   * @return "SORN" or formatted liability
   */
  def formatLiabilityForMultiView(vehicle: Vehicle) = {
    if (vehicle.taxStatus == Some(TaxStatus.SORN)) {
      Some(TaxStatus.SORN)
    } else {
      vehicle.liability.map(PortalDateFormatter.shortDateFormat)
    }
  }
}

case class VehicleListViewModel(
                                 registrationNumber: Option[String],
                                 make: Option[String],
                                 liability: Option[String],
                                 firstRegistration: Option[String],
                                 fleetNumber: Option[String], //do we really need that one ?
                                 taxCode: Option[String]) {//do we need licensing type really ?

  def url(searchFilters: VehicleRefinedSearchData) =
    URLBuilderUtils.buildUrl(routes.ViewFleetRecords.redirectToVehicleDetails.url + "?" +"registrationNumber=" + registrationNumber.get + "&vehicleMake="+ make.get, searchFilters.toQueryMap)

}

