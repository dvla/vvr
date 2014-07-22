package uk.gov.dvla.services.enquiry

import uk.gov.dvla.domain.{VehicleList, FleetHeader, Vehicle}
import uk.gov.dvla.services.ManagedService
import uk.gov.dvla.domain.criteria.FilterCriteria


object VehicleEnquiry {
  val VEHICLE_SERVICE_ENDPOINT = "/vehicles"
}

trait VehicleEnquiry extends ManagedService {
  def getVehicle(registrationNumber: String): Option[Vehicle]

  def getVehiclesByFleet(fleetNumber: String, offset: Int, limit: Int, criteria: FilterCriteria): VehicleList

  def getFleetHeaderData(fleetNumber: String): Option[FleetHeader]

}