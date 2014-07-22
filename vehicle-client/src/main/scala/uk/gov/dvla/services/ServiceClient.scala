package uk.gov.dvla.services

import com.sun.jersey.api.client.{WebResource, Client}
import java.net.URL
import uk.gov.dvla.services.ManagedService.HEALTH_CHECK_ENDPOINT
import com.sun.jersey.core.util.MultivaluedMapImpl


abstract class ServiceClient(client: Client, baseUrl: URL, baseAdminUrl: URL) extends ManagedService {

  def isAlive: Boolean = {
    val healthCheckUrl = s"$baseAdminUrl$HEALTH_CHECK_ENDPOINT"
    val resource: WebResource = client.resource(healthCheckUrl)
    resource.head.getStatus == 200
  }

  def start {
  }

  def stop {
    client.destroy
  }

  implicit def multivaluedMap2sMap(map: Map[String, String]) = {
    val multivaluedMap = new MultivaluedMapImpl()
    map.foreach {
      e => multivaluedMap.add(e._1, e._2)
    }
    multivaluedMap
  }

}
