package uk.gov.dvla.services.enquiry.reference

import com.yammer.dropwizard.config.Configuration
import com.yammer.dropwizard.db.DatabaseConfiguration
import net.devlab722.dropwizard.bundle.{LogstashLogbackEncoderConfiguration => LogstashConf }

case class ReferenceEnquiryServiceConfiguration(
            logstashLogbackEncoderConfiguration: LogstashConf = new LogstashConf,
            database: DatabaseConfiguration = new DatabaseConfiguration
            ) extends Configuration
