package uk.gov.dvla.services.common

import com.yammer.metrics.core.HealthCheck
import uk.gov.dvla.services.ManagedService
import com.yammer.metrics.core.HealthCheck.Result

final class ManagedServiceHealthCheck(service:ManagedService) extends HealthCheck(service.getName) {



  protected def check: HealthCheck.Result = {
    try {
       if (service_i.isAlive) Result.healthy else Result.unhealthy(service_i.getName + " is down!")
    }
    catch {
      case e: Exception => Result.unhealthy(e)

    }
  }

  private var service_i: ManagedService = null
}