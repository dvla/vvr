package uk.gov.dvla.services.enquiry.reference.resources

import com.yammer.dropwizard.testing._
import org.junit.Test
import org.mockito.Mockito._
import com.sun.jersey.api.client.ClientResponse
import uk.gov.dvla.services.enquiry.reference.jdbi.ReferenceEnquiryDao
import java.util


class ReferenceResourceDropwizardTest extends ResourceTest {

  private val referenceEnquiryDao: ReferenceEnquiryDao = mock(classOf[ReferenceEnquiryDao])

  override def setUpResources {
    when(referenceEnquiryDao.findMakes()).thenReturn(new util.ArrayList[String]())
    addResource(new ReferenceEnquiryResource(referenceEnquiryDao))
  }

  @Test
  def shouldReturnFoundWhenFindMakes {
    assert(client.resource("/reference/makes").get(classOf[ClientResponse]).getStatus == 200)
  }

  @Test
  def shouldReturnNotFoundWhenFindMakes {
    assert(client.resource("/reference/makeswrong").get(classOf[ClientResponse]).getStatus == 404)
  }

}
