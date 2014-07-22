package uk.gov.dvla.services.filter

import org.scalatest.{BeforeAndAfterAll, Matchers, FlatSpec}
import uk.gov.dvla.services.enquiry.VehicleEnquiry
import org.mockito.Mockito.{mock, when}
import uk.gov.dvla.services.filter.vehicle.resources.VehicleFilterResource
import uk.gov.dvla.domain.{LicensingType, Vehicle}
import java.util.Date
import uk.gov.dvla.services.exception.{NotMatchingDataException, NotFoundDataException}


class VehicleFilterResourceSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  private val client = mock(classOf[VehicleEnquiry])
  private val vehicle = new Vehicle("QWERTY", "Mazda", new Date(), "123456", "38", "2", "Private Light Goods (PLG)", "red", "2 Axle rigid body", "2 Door Saloon", LicensingType.LICENSED)

  override def beforeAll() {
    when(client.getVehicle("QWERTY")).thenReturn(Option(vehicle))
  }

  it should "throw NotFoundDataException when vehicle registration number and make does not match" in {
    a [NotFoundDataException] should be thrownBy {
      new VehicleFilterResource(client).findVehicle("123456", "QWERTY", "FIAT")
    }
  }

  it should "throw NotMatchingDataException when a vehicle does not match with fleet number" in {
    a [NotMatchingDataException] should be thrownBy {
      new VehicleFilterResource(client).findVehicle("654321", "QWERTY", "Mazda")
    }
  }

  "Get the vehicle with correct parameters" should "return the vehicle" in {
    val vehicleFilterResource = new VehicleFilterResource(client)

    val retrievedVehicle = vehicleFilterResource.findVehicle("123456", "QWERTY", "Mazda")

    retrievedVehicle should be (vehicle)
  }

}