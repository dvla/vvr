package uk.gov.dvla.services.filter.vehicle

import com.sun.jersey.api.client.Client
import com.yammer.dropwizard.ScalaService
import com.yammer.dropwizard.client.JerseyClientBuilder
import com.yammer.dropwizard.config.Bootstrap
import com.yammer.dropwizard.config.Environment
import uk.gov.dvla.services.enquiry.VehicleEnquiry
import uk.gov.dvla.services.enquiry.VehicleEnquiryClient
import uk.gov.dvla.services.filter.vehicle.resources.VehicleFilterResource
import com.yammer.dropwizard.bundles.ScalaBundle
import uk.gov.dvla.services.common.{QueryMapProvider, ServiceDateFormat}
import com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider
import com.wordnik.swagger.config.ScannerFactory
import com.wordnik.swagger.jaxrs.config.DefaultJaxrsScanner
import com.wordnik.swagger.reader.ClassReaders
import com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader
import com.wordnik.swagger.jaxrs.listing._
import org.slf4j.LoggerFactory
import net.devlab722.dropwizard.bundle.LogstashLogbackEncoderBundle


object VehicleFilterService extends ScalaService[VehicleFilterConfiguration] {

  private val logger = LoggerFactory.getLogger(getClass)

  def initialize(bootstrap: Bootstrap[VehicleFilterConfiguration]) {
    bootstrap.setName("vehicle-filter-service")
    bootstrap.addBundle(new ScalaBundle)
    bootstrap.addBundle(new LogstashLogbackEncoderBundle[VehicleFilterConfiguration]() {
      override def getConfiguration(configuration :VehicleFilterConfiguration) =
        configuration.logstashLogbackEncoderConfiguration
    })
    logger.debug("Filter service has been initialized")
  }

  def run(configuration: VehicleFilterConfiguration, environment: Environment) {
    environment.getObjectMapperFactory.setDateFormat(ServiceDateFormat.DEFAULT)
    val vehicleEnquiryClient: VehicleEnquiry = prepareClient(configuration, environment)
    environment.addResource(new VehicleFilterResource(vehicleEnquiryClient))
    environment.addResource(new ApiListingResourceJSON)
    environment.addProvider(new ApiDeclarationProvider)
    environment.addProvider(new ResourceListingProvider)
    environment.addProvider(new QueryMapProvider)
    ScannerFactory.setScanner(new DefaultJaxrsScanner)
    ClassReaders.setReader(new DefaultJaxrsApiReader)
    environment.addHealthCheck(new VehicleEnquiryServiceHealthCheck("vehicle-enquiry-service", configuration))

    logger.debug("Filter service has been configured successfully")
  }

  private def prepareClient(configuration: VehicleFilterConfiguration, environment: Environment): VehicleEnquiry = {
    val client: Client = new JerseyClientBuilder().using(configuration.getJerseyClientConfiguration).using(environment).build
    val vehicleClient = new VehicleEnquiryClient(client, configuration.getBaseUrl, configuration.getBaseAdminUrl)

    logger.debug("Vehicle client created succesfully")
    vehicleClient
  }
}