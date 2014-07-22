package uk.gov.dvla.services.enquiry.reference.resources

import org.scalatest._
import org.mockito.Mockito.mock
import uk.gov.dvla.services.enquiry.reference.jdbi.ReferenceEnquiryDao
import java.util


class ReferenceResourceSpec extends FlatSpec with Matchers with GivenWhenThen {

  val referenceEnquiryDao = mock(classOf[ReferenceEnquiryDao])

  "List makes" should "return a list with one element" in {

    Given("A List of Makes with one make")
    val makes: util.ArrayList[String] = new util.ArrayList[String]
    makes.add("Audi")

    When("Mock of ReferenceEnquiryDAO is Invoked")

    org.mockito.Mockito.when(referenceEnquiryDao.findMakes()).thenReturn(makes)
    val referenceEnquiryResource = new ReferenceEnquiryResource(referenceEnquiryDao)

    Then("The Returned List contains one make")
    referenceEnquiryResource.listMakes().size should be(1)
  }

  "List makes while there are no makes" should "return an empty list" in {
    Given("An Empty List of Makes")
    val makes = new util.ArrayList[String]()

    When("Mock of ReferenceEnquiryDAO is Invoked")
    org.mockito.Mockito.when(referenceEnquiryDao.findMakes()).thenReturn(makes)
    val referenceEnquiryResource = new ReferenceEnquiryResource(referenceEnquiryDao)

    Then("The Returned List should be empty")

    referenceEnquiryResource.listMakes().size should be(0)
  }
}
