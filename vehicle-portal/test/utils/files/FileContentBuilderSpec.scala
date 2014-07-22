package utils.files

import org.scalatest.{Matchers, FlatSpec}
import java.nio.charset.Charset
import java.io.InputStream
import uk.gov.dvla.domain.{TaxStatus, LicensingType, Vehicle}
import java.util.Date

class FileContentBuilderSpec extends FlatSpec with Matchers {

  val fileContentBuilderMock = new FileContentBuilder {
    override def create(headerMapper: Seq[(String, String, Option[Formatter])], records: Seq[AnyRef], charset: Charset): InputStream = null
  }

  "FileContentBuilder.field value" should "return a specified, properly formatted field from an object" in {
    val _2000_09_23: Long = 969663600000L
    val vehicle = new Vehicle("A9", "FORD", new Date(_2000_09_23), "123456", "G", "PLG", "Petrol", "red", "2 Axle rigid body", "2 Door Saloon", LicensingType.LICENSED, Some(new Date(_2000_09_23)), None, Some(2))

    fileContentBuilderMock.fieldValue(vehicle, "registrationNumber", None) should be("A9")
    fileContentBuilderMock.fieldValue(vehicle, "make", None) should be("FORD")
    fileContentBuilderMock.fieldValue(vehicle, "liability", Some(DateFormatter)) should be("2000-09-23")
    fileContentBuilderMock.fieldValue(vehicle, "firstRegistration", Some(DateFormatter)) should be("2000-09-23")
    fileContentBuilderMock.fieldValue(vehicle, "fleetNumber", None) should be("123456")
    fileContentBuilderMock.fieldValue(vehicle, "taxCode", None) should be("G")
    fileContentBuilderMock.fieldValue(vehicle, "cylinderCapacity", None) should be("")
    fileContentBuilderMock.fieldValue(vehicle, "co2Emissions", None) should be("2")
    fileContentBuilderMock.fieldValue(vehicle, "fuelType", None) should be("Petrol")
    fileContentBuilderMock.fieldValue(vehicle, "colour", None) should be("red")
    fileContentBuilderMock.fieldValue(vehicle, "taxBand", None) should be("A")
    fileContentBuilderMock.fieldValue(vehicle, "taxStatus", None) should be(TaxStatus.EXPIRED)
    fileContentBuilderMock.fieldValue(vehicle, "licensingType", None) should be(LicensingType.LICENSED)
  }

}
