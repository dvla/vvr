package uk.gov.dvla.services.filter.vehicle

import com.fasterxml.jackson.annotation.JsonProperty
import com.yammer.dropwizard.client.JerseyClientConfiguration
import com.yammer.dropwizard.config.Configuration
import net.devlab722.dropwizard.bundle.{LogstashLogbackEncoderConfiguration => LogstashConf }
import javax.validation.Valid
import javax.validation.constraints.NotNull
import java.net.MalformedURLException
import java.net.URL

case class VehicleFilterConfiguration(logstashLogbackEncoderConfiguration: LogstashConf = new LogstashConf
           ) extends Configuration {

  def getBaseUrl: URL = {
    this.baseUrl
  }

  def getBaseAdminUrl: URL = {
    if (baseAdminUrl != null)
      baseAdminUrl
    else {
      val port: Int = baseUrl.getPort + 1
      try {
            new URL(baseUrl.getProtocol, baseUrl.getHost, port, baseUrl.getFile)
      }
      catch {
        case e: MalformedURLException => {
          throw new RuntimeException(e)
        }
      }
    }
  }

  def getJerseyClientConfiguration: JerseyClientConfiguration = {
    httpClient
  }

  @Valid
  @NotNull
  @JsonProperty private var baseUrl: URL = null

  @Valid
  @JsonProperty private var baseAdminUrl: URL = null

  @Valid
  @NotNull
  @JsonProperty private var httpClient: JerseyClientConfiguration = new JerseyClientConfiguration
}