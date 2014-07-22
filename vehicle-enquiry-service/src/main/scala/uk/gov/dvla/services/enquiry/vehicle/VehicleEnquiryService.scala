package uk.gov.dvla.services.enquiry.vehicle

import com.yammer.dropwizard.ScalaService
import com.yammer.dropwizard.config.{Environment, Bootstrap}

import com.yammer.dropwizard.bundles.ScalaBundle
import com.yammer.dropwizard.jdbi.DBIFactory
import org.skife.jdbi.v2.DBI

import com.wordnik.swagger.jaxrs.listing.{ApiDeclarationProvider, ResourceListingProvider, ApiListingResourceJSON}
import com.wordnik.swagger.config.ScannerFactory
import com.wordnik.swagger.jaxrs.config.DefaultJaxrsScanner
import com.wordnik.swagger.reader.ClassReaders
import com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader

import uk.gov.dvla.services.enquiry.vehicle.jdbi.{VehicleRefinedSearchDao, VehicleDao}
import uk.gov.dvla.services.enquiry.vehicle.resources.VehicleResource
import org.slf4j.LoggerFactory
import com.gilt.jdbi.OptionContainerFactory
import com.wordnik.swagger.jaxrs.listing._
import net.devlab722.dropwizard.bundle.LogstashLogbackEncoderBundle
import uk.gov.dvla.services.enquiry.vehicle.VehicleEnquiryServiceConfiguration
import uk.gov.dvla.time.TimeUtils
import uk.gov.dvla.services.common.QueryMapProvider


object VehicleEnquiryService extends ScalaService[VehicleEnquiryServiceConfiguration] {

  private val logger = LoggerFactory.getLogger(getClass)

  def initialize(bootstrap: Bootstrap[VehicleEnquiryServiceConfiguration]) {
    bootstrap.setName("enquiry-service")
    bootstrap.addBundle(new ScalaBundle)
    bootstrap.addBundle(new LogstashLogbackEncoderBundle[VehicleEnquiryServiceConfiguration]() {
      def getConfiguration(configuration: VehicleEnquiryServiceConfiguration) =
        configuration.logstashLogbackEncoderConfiguration
    })
    logger.info("Enquiry service has been initialized")
  }

  def run(configuration: VehicleEnquiryServiceConfiguration, environment: Environment) {
    val factory: DBIFactory = new DBIFactory()
    val jdbi: DBI = factory.build(environment, configuration.database, "postgresql")
    jdbi.registerContainerFactory(new OptionContainerFactory)
    environment.isRunning
    val dao:VehicleDao = jdbi.onDemand(classOf[VehicleDao])
    val vehicleRefinedSearchDao = new VehicleRefinedSearchDao(jdbi)

    environment.addResource(new VehicleResource(dao, TimeUtils, vehicleRefinedSearchDao, jdbi))
    environment.addResource(new ApiListingResourceJSON)
    environment.addProvider(new ApiDeclarationProvider)
    environment.addProvider(new ResourceListingProvider)
    environment.addProvider(new QueryMapProvider)
    ScannerFactory.setScanner(new DefaultJaxrsScanner)
    ClassReaders.setReader(new DefaultJaxrsApiReader)

    logger.info("Enquiry service has been configured successfully")
  }
}

