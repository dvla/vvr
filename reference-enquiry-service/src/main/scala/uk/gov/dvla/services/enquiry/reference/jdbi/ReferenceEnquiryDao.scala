package uk.gov.dvla.services.enquiry.reference.jdbi

import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper

trait ReferenceEnquiryDao {
  @SqlQuery("select make from enquiry.make")
  def findMakes():java.util.List[String]
}
