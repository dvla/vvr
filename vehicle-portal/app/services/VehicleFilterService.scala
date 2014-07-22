package services

import play.api.libs.ws.{WS, Response}
import scala.concurrent.Future
import play.api.Play
import javax.naming.ConfigurationException
import model.VehicleData
import org.slf4j.LoggerFactory
import model.VehicleRefinedSearchData

trait VehicleFilterService {
  def searchVehicle(vehicleSearchForm: VehicleData): Future[Response]
  def getFleetRecords(fleetNumber: String, offset: Int, limit: Int, searchFilter: VehicleRefinedSearchData): Future[Response]
  def getFleetHeaderData(fleetNumber: String): Future[Response]
}

object VehicleFilterService extends VehicleFilterService {

  val logger = LoggerFactory.getLogger(getClass)

  lazy val filterUrl = Play.current.configuration.getString("vehicleFilterService.url")
    .getOrElse(throw new ConfigurationException("Vehicle Filter Service is not properly configured in Vehicle Portal"))

  def searchVehicle(vehicleSearchForm: VehicleData): Future[Response] = {
    WS.url(filterUrl).withQueryString("fleetNumber" -> vehicleSearchForm.fleetNumber,
      "registrationNumber" -> vehicleSearchForm.registrationNumber,
      "make" -> vehicleSearchForm.vehicleMake).get()
  }

  def getFleetRecords(fleetNumber: String, offset: Int, limit: Int, searchFilter: VehicleRefinedSearchData): Future[Response] = {
    val queryFilterParams = searchFilter.flattenToServiceQueryParams

    WS.url(s"$filterUrl/fleetRecords/$fleetNumber").withQueryString(
      Seq(("offset" -> offset.toString), ("limit" -> limit.toString)) ++ queryFilterParams: _*
    ).get
  }

  def getFleetHeaderData(fleetNumber: String): Future[Response] = {
    WS.url(s"$filterUrl/fleet/header/$fleetNumber").get()
  }

}
