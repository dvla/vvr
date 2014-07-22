package uk.gov.dvla.services.enquiry.vehicle

import com.yammer.dropwizard.config.Configuration
import com.yammer.dropwizard.db.DatabaseConfiguration
import net.devlab722.dropwizard.bundle.{LogstashLogbackEncoderConfiguration => LogstashConf}

case class VehicleEnquiryServiceConfiguration(
             logstashLogbackEncoderConfiguration: LogstashConf = new LogstashConf,
             database: DatabaseConfiguration = new DatabaseConfiguration
             ) extends Configuration

