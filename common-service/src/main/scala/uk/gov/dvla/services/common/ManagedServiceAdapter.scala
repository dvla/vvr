package uk.gov.dvla.services.common

import com.yammer.dropwizard.lifecycle.Managed
import uk.gov.dvla.services.ManagedService

final class ManagedServiceAdapter extends Managed {
  def this(service: ManagedService) {
    this()
    this.service_i = service
  }

  def start {
    this.service_i.start
  }

  def stop {
    this.service_i.stop
  }

  private var service_i: ManagedService = null
}