package model

import play.api.libs.json.JsValue
import uk.gov.dvla.domain.FleetFilter

case class FleetHeader(
  vehicleCount: Int,
  ageAlertCount: Int,
  liabilityAlertCount:Int,
  activeFilter:String){

  def getFilteredVehiclesCount() = {
    activeFilter match {
      case FleetFilter.ALL => vehicleCount
      case FleetFilter.DUE => liabilityAlertCount
      case FleetFilter.OLD => ageAlertCount
      case _ => vehicleCount
    }
  }
}

object FleetHeader {
  def apply(json: JsValue, filter:String = ""): FleetHeader = {
    val vehicleCount = (json \ "vehicleCount").as[Int]
    val ageAlertCount = (json \ "ageAlertCount").as[Int]
    val liabilityAlertCount = (json \ "liabilityAlertCount").as[Int]
    FleetHeader(vehicleCount, ageAlertCount, liabilityAlertCount, filter)
  }
}