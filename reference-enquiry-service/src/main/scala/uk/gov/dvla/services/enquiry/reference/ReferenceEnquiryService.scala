package uk.gov.dvla.services.enquiry.reference

import com.yammer.dropwizard.ScalaService
import com.yammer.dropwizard.config.{Environment, Bootstrap}

import com.yammer.dropwizard.bundles.ScalaBundle
import com.yammer.dropwizard.jdbi.DBIFactory
import org.skife.jdbi.v2.DBI
import org.slf4j.LoggerFactory
import com.gilt.jdbi.OptionContainerFactory
import com.wordnik.swagger.jaxrs.listing.{ApiDeclarationProvider, ResourceListingProvider, ApiListingResourceJSON}
import com.wordnik.swagger.config.ScannerFactory
import com.wordnik.swagger.jaxrs.config.DefaultJaxrsScanner
import com.wordnik.swagger.reader.ClassReaders
import com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader
import uk.gov.dvla.services.enquiry.reference.jdbi.ReferenceEnquiryDao
import uk.gov.dvla.services.enquiry.reference.resources.ReferenceEnquiryResource
import net.devlab722.dropwizard.bundle.{LogstashLogbackEncoderConfiguration, LogstashLogbackEncoderBundle}



object ReferenceEnquiryService extends ScalaService[ReferenceEnquiryServiceConfiguration] {

  private val logger = LoggerFactory.getLogger(getClass)

  def initialize(bootstrap: Bootstrap[ReferenceEnquiryServiceConfiguration]) {
    bootstrap.setName("reference-enquiry-service")
    bootstrap.addBundle(new ScalaBundle)
    bootstrap.addBundle(new LogstashLogbackEncoderBundle[ReferenceEnquiryServiceConfiguration]() {
      def getConfiguration(configuration :ReferenceEnquiryServiceConfiguration)  =
        configuration.logstashLogbackEncoderConfiguration
    })
    logger.info("Reference Enquiry service has been initialized")
  }

  def run(configuration: ReferenceEnquiryServiceConfiguration, environment: Environment) {
    val factory: DBIFactory = new DBIFactory()
    val jdbi: DBI = factory.build(environment, configuration.database, "postgresql")
    jdbi.registerContainerFactory(new OptionContainerFactory)
    environment.isRunning
    val dao: ReferenceEnquiryDao = jdbi.onDemand(classOf[ReferenceEnquiryDao])
    environment.addResource(new ReferenceEnquiryResource(dao))

    environment.addResource(new ApiListingResourceJSON)
    environment.addProvider(new ApiDeclarationProvider)
    environment.addProvider(new ResourceListingProvider)
    ScannerFactory.setScanner(new DefaultJaxrsScanner)
    ClassReaders.setReader(new DefaultJaxrsApiReader)

    logger.info("Reference Enquiry service has been configured successfully")
  }
}