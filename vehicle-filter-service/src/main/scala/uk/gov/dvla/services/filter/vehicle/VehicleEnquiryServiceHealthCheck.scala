package uk.gov.dvla.services.filter.vehicle

import com.yammer.metrics.core.HealthCheck
import com.yammer.metrics.core.HealthCheck.Result
import java.lang.Exception
import com.sun.jersey.api.client.{ClientResponse, Client}
import uk.gov.dvla.services.enquiry.VehicleEnquiry
import uk.gov.dvla.services.ManagedService
import org.slf4j.LoggerFactory

class VehicleEnquiryServiceHealthCheck(name: String, config: VehicleFilterConfiguration) extends HealthCheck(name) {

  private val logger = LoggerFactory.getLogger(getClass)

  def check(): Result = {
    try {
      val client = Client.create()
      val webResource = client.resource(config.getBaseAdminUrl + ManagedService.HEALTH_CHECK_ENDPOINT)

      val responseStatus = webResource.get(classOf[ClientResponse]).getClientResponseStatus
      if (responseStatus == ClientResponse.Status.OK)
        Result.healthy()
      else {
        val message = s"${config.getBaseUrl}${VehicleEnquiry.VEHICLE_SERVICE_ENDPOINT}/ did not respond with 200, Vehicle Enquiry Service is NOT OK"
        logger.warn(message)
        Result.unhealthy(message)
      }
    } catch {
      case e: Exception => {
        logger.warn("The service throws an exception", e)
        Result.unhealthy(s"The service throws an exception $e")
      }
    }
  }

}