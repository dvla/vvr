package uk.gov.dvla.services.enquiry

import com.sun.jersey.api.client.{GenericType, ClientResponse, Client, WebResource}
import uk.gov.dvla.domain.{VehicleList, FleetHeader, Vehicle}
import java.net.URL
import uk.gov.dvla.services.ServiceClient
import scala.collection.JavaConverters._
import uk.gov.dvla.domain.criteria.FilterCriteria


class VehicleEnquiryClient(client: Client, baseUrl: URL, baseAdminUrl: URL)
  extends ServiceClient(client, baseUrl, baseAdminUrl) with VehicleEnquiry {

  def getVehicle(registrationNumber: String): Option[Vehicle] = {
    val resource: WebResource = client.resource(resourceUrl + "/" + registrationNumber)
    val response = resource.accept("application/json").get(classOf[ClientResponse])
    if (response.getClientResponseStatus == ClientResponse.Status.OK)
      Option(response.getEntity(classOf[Vehicle]))
    else
      None
  }

  def resourceUrl = baseUrl + VehicleEnquiry.VEHICLE_SERVICE_ENDPOINT

  override def getName: String = "vehicle-enquiry-service"

  def getVehiclesByFleet(fleetNumber: String, offset: Int, limit: Int, filterCriteria: FilterCriteria): VehicleList = {
    val resource: WebResource = client.resource(s"$resourceUrl/fleet/$fleetNumber")
      .queryParams(Map("offset" -> offset.toString, "limit" -> limit.toString)
      ++ filterCriteria.flattenToServiceQueryParams)
    val response = resource.accept("application/json").get(classOf[ClientResponse])
    if (response.getClientResponseStatus == ClientResponse.Status.OK)
      response.getEntity(classOf[VehicleList])
    else
      VehicleList.empty
  }

  def getFleetHeaderData(fleetNumber: String): Option[FleetHeader] = {
    val resource: WebResource = client.resource(s"$resourceUrl/fleet/header/$fleetNumber")
    val response = resource.accept("application/json").get(classOf[ClientResponse])
    if (response.getClientResponseStatus == ClientResponse.Status.OK)
      Option(response.getEntity(classOf[FleetHeader]))
    else
      None
  }

}