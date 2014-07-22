package utils.files

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.dvla.domain.{LicensingType, Vehicle}
import java.util.Date
import java.io.{InputStream, StringWriter}
import java.nio.charset.StandardCharsets
import org.apache.commons.io.IOUtils
import controllers.FileExportFleetRecords
import model.{VehicleListExportViewModel, VehicleListViewModel}


class CSVFileContentBuilderSpec extends FlatSpec with Matchers {

  val _2000_09_23: Long = 969663600000L
  val _2015_01_10: Long = 1420866000000L
  val vehicle1 = VehicleListExportViewModel(
    new Vehicle("A9", "FORD", new Date(_2000_09_23), "123456", "G", "PLG", "Petrol", "red", "2 Axle rigid body", "Saloon",
      LicensingType.LICENSED, Some(new Date(_2000_09_23)), Some(1), Some(2), Some(3500), Some(5), Some(8),
      Some(3000), Some(new Date(_2000_09_23)), Some("M1"), motExpiry = Some(new Date(_2015_01_10)))
  )
  val vehicle2 = VehicleListExportViewModel(
    new Vehicle("V23", "FORD", new Date(_2000_09_23), "23456-", "F", "PLG", "Heavy Oil (Diesel)", "yellow", "2 Axle rigid body", "Saloon",
      LicensingType.LICENSED, Some(new Date(_2000_09_23)), Some(1), Some(2), motExpiry = Some(new Date(_2015_01_10)))
  )
  val vehicle3 = VehicleListExportViewModel(
    new Vehicle("AAB911A", "FORD", new Date(_2000_09_23), "000000", "A", "PLG", "Electric", "red", "2 Axle rigid body", "Saloon",
      LicensingType.LICENSED, Some(new Date(_2000_09_23)), Some(1), Some(2), motExpiry = Some(new Date(_2015_01_10)))
  )
  val vehicle4 = VehicleListExportViewModel(
    new Vehicle("BT54KNK", "Ford", new Date(_2000_09_23), "000000", "A", "PLG", "Electric", "Blue", "2 Axle rigid body", "Saloon",
      LicensingType.SORN, None, Some(1), Some(2), motExpiry = Some(new Date(_2015_01_10)))
  )

  "CSVFileContentBuilder.create" should "return csv content" in {
    val expectedCSVContent =
      """Registration number,Vehicle make,Tax status,Date of liability,MOT expiry,Date of first registration,Date of first registration in UK,Tax class,Cylinder Capacity,CO2 emissions,Fuel type,Tax band,Vehicle colour,Body type,Revenue weight,Mass in service,Number of seats,Standing capacity,Wheelplan,Vehicle category
      |A9,"Ford","Expired",23 Sep 2000,10 Jan 2015,23 Sep 2000,23 Sep 2000,"G - PLG",1,2,Petrol,A,Red,"Saloon",3000,3500,5,8,"2 Axle rigid body",M1
      |V23,"Ford","Expired",23 Sep 2000,10 Jan 2015,23 Sep 2000,,"F - PLG",1,2,Heavy Oil (Diesel),A,Yellow,"Saloon",,,,,"2 Axle rigid body",
      |AAB911A,"Ford","Expired",23 Sep 2000,10 Jan 2015,23 Sep 2000,,"A - PLG",1,2,Electric,A,Red,"Saloon",,,,,"2 Axle rigid body",
      |BT54KNK,"Ford","SORN",N/A,10 Jan 2015,23 Sep 2000,,"A - PLG",1,2,Electric,A,Blue,"Saloon",,,,,"2 Axle rigid body",""".stripMargin
    val actualCSVContentStream = CSVFileContentBuilder.create(FileExportFleetRecords.vehicleToFileMapper, Seq(vehicle1, vehicle2, vehicle3, vehicle4), StandardCharsets.UTF_8)
    val actual = convertToString(actualCSVContentStream, "UTF-8")

    actual should be(expectedCSVContent)
  }

  private def convertToString(inputStream: InputStream, encoding: String): String = {
    val writer = new StringWriter()
    IOUtils.copy(inputStream, writer, encoding)
    writer.toString
  }

}
