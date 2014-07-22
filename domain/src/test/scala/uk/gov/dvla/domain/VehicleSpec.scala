package uk.gov.dvla.domain

import org.scalatest._
import java.util.{Date, Calendar}
import play.api.libs.json.{JsResultException, Json}
import org.joda.time.DateTime
import scala.Some

class VehicleSpec extends FlatSpec with Matchers {

  private def vehicleWithRegAndCo2(dateOfRegistration:Date, co2:Int) =
    new Vehicle(
      "AAA123A", "VW", dateOfRegistration,
      "123456", "A", "PLG", "Petrol", "white", "2 Axle rigid body", "2 Door Saloon",
      LicensingType.LICENSED,
      Some(DateTime.parse("2014-02-22").toDate),
      Some(1600), Some(co2), Some(3500), Some(5), Some(8), Some(2000),
      Some(dateOfRegistration), Some("M1"))


  // tax status
  /**
   * US917
   * Where the date of liability for the vehicle is in the past then output = Expired
   */
  "Where the date of liability for the vehicle is in the past" should "tax status should be 'Expired'" in {
    val cal = Calendar.getInstance
    cal.roll(Calendar.DAY_OF_YEAR, -1)
    assert(Vehicle.getTaxStatus(Option(cal.getTime), LicensingType.LICENSED) == Some(TaxStatus.EXPIRED))
  }

  /**
   * US019
   * Where the date of liability for the vehicle is in the past, but it's SORN then output = SORN
   */
  "Where the date of liability for the vehicle is in the past, but it's SORN" should "tax status be 'SORN'" in {
    val cal = Calendar.getInstance
    cal.roll(Calendar.DAY_OF_YEAR, -1)
    assert(Vehicle.getTaxStatus(Option(cal.getTime), LicensingType.SORN) == Some(TaxStatus.SORN))
  }

  /**
   * US1019 Scenario 3 Note && US917
   * Where the date of liability for the vehicle is in the future but is less than 12mths then output = Taxed
   */
  "Where the date of liability for the vehicle is in the future but is less than 12mths" should "tax status should be 'Taxed'" in {
    val cal = Calendar.getInstance
    cal.roll(Calendar.MONTH, 2)
    assert(Vehicle.getTaxStatus(Option(cal.getTime), LicensingType.LICENSED) == Some(TaxStatus.TAXED))
  }

  /**
   * US1023 Scenario 2
   * Where the date of liability for the vehicle is in the future but is less than 1 month then output = Tax Due
   */
  "Where the date of liability for the vehicle is in the future but is less than 1 month" should "tax status should be 'Tax Due'" in {
    val cal = Calendar.getInstance
    cal.roll(Calendar.DAY_OF_YEAR, 1)
    assert(Vehicle.getTaxStatus(Option(cal.getTime), LicensingType.LICENSED) == Some(TaxStatus.TAX_DUE))
  }

  /**
   * US1019 Scenario 3
   * Where the vehicle is not SORN (see Scenario 1)
   * and the date of liability for the vehicle is more than 12 months in the future = Taxed
   */
  "Where the date of liability for not SORN vehicle is over 12mths in the future" should "tax status be 'Taxed'" in {
    val cal = Calendar.getInstance
    cal.roll(Calendar.YEAR, 2)
    assert(Vehicle.getTaxStatus(Option(cal.getTime), LicensingType.LICENSED) == Some(TaxStatus.TAXED))
  }

  /**
   * US1019 Scenario 2
   * ...And the vehicle is determined to be SORN (see Scenario 1)
   * Then the Tax Status shall be set to SORN
   */
  "Where the vehicle is SORN " should "tax status be 'SORN'" in {
    val cal = Calendar.getInstance
    assert(Vehicle.getTaxStatus(Option(cal.getTime), LicensingType.SORN) == Some(TaxStatus.SORN))
  }

  "Where the date of liability for the vehicle is null" should "throw NullPointerException" in {
    a [NullPointerException] should be thrownBy {
      Vehicle.getTaxStatus(null, LicensingType.LICENSED)
    }
  }

  "Where the date of liability for the vehicle is None" should "tax status should be None" in {
    assert(Vehicle.getTaxStatus(None, LicensingType.LICENSED) == None)
  }

  // tax band
  /*
   * The value for “Tax band” shall be calculated based on the following:
   * CO2 value 0-100 = Band A
   * 101 to 110 = Band B
   * 111 to 120 = Band C
   * 121 to 130 = Band D
   * 131 to 140 = Band E
   * 141 to 150 = Band F
   * 151 to 165 = Band G
   * 166 to 175 = Band H
   * 176 to 185 = Band I
   * 186 to 200 = Band J
   * 201 to 225 = Band K
   *
   * Where date of registration is before 23/03/2006 and CO2 value is over 225 = Band K
   *
   * Otherwise
   * 226 to 255 = Band L
   * Over 255 = Band M
   */
  "Where CO2 value 1-100" should "tax band be 'Band A'" in {
    assert(getTaxBand(1) == Some("A"))
    assert(getTaxBand(100) == Some("A"))
  }
  "Where CO2 value 101 to 110" should "tax band be 'Band B'" in {
    assert(getTaxBand(101) == Some("B"))
    assert(getTaxBand(110) == Some("B"))
  }
  "Where CO2 value 111 to 120" should "tax band be 'Band C'" in {
    assert(getTaxBand(111) == Some("C"))
    assert(getTaxBand(120) == Some("C"))
  }
  "Where CO2 value 121 to 130" should "tax band be 'Band D'" in {
    assert(getTaxBand(121) == Some("D"))
    assert(getTaxBand(130) == Some("D"))
  }
  "Where CO2 value 131 to 140" should "tax band be 'Band E'" in {
    assert(getTaxBand(131) == Some("E"))
    assert(getTaxBand(140) == Some("E"))
  }
  "Where CO2 value 141 to 150" should "tax band be 'Band F'" in {
    assert(getTaxBand(141) == Some("F"))
    assert(getTaxBand(150) == Some("F"))
  }
  "Where CO2 value 151 to 165" should "tax band be 'Band G'" in {
    assert(getTaxBand(151) == Some("G"))
    assert(getTaxBand(165) == Some("G"))
  }
  "Where CO2 value 166 to 175" should "tax band be 'Band H'" in {
    assert(getTaxBand(166) == Some("H"))
    assert(getTaxBand(175) == Some("H"))
  }
  "Where CO2 value 176 to 185" should "tax band be 'Band I'" in {
    assert(getTaxBand(176) == Some("I"))
    assert(getTaxBand(185) == Some("I"))
  }
  "Where CO2 value 186 to 200" should "tax band be 'Band J'" in {
    assert(getTaxBand(186) == Some("J"))
    assert(getTaxBand(200) == Some("J"))
  }
  "Where CO2 value 201 to 225" should "tax band be 'Band K'" in {
    assert(getTaxBand(201) == Some("K"))
    assert(getTaxBand(225) == Some("K"))
  }
  "Where date of registration is before 23/03/2006 and CO2 value is over 225" should "tax band be 'Band K'" in {
    val vehicle = vehicleWithRegAndCo2(DateTime.parse("2006-02-22").toDate, 226)
    assert(vehicle.taxBand == Some("K"))
  }
  "Where date of registration is after  23/03/2006 and CO2 value 226 to 255" should "tax band be 'Band L'" in {
    val vehicle = vehicleWithRegAndCo2(Calendar.getInstance.getTime, 255)
    assert(vehicle.taxBand == Some("L"))
  }
  "Where date of registration is after  23/03/2006 and CO2 value over 255" should "tax band be 'Band M'" in {
    val vehicle = vehicleWithRegAndCo2(Calendar.getInstance.getTime, 256)
    assert(vehicle.taxBand == Some("M"))
  }

  private def getTaxBand(co2: Int) = {
    Vehicle.getTaxBand(Calendar.getInstance.getTime, co2)
  }

  /**
   * test vehicle construction and Json parsing when optional fields are provided
   */
  "Vehicle.apply(args)" should "create a vehicle if required and optional fields are provided" in {
    val json = Json.parse(
      """{
        "registrationNumber": "AB07 XYZ",
        "make":"FORD",
        "taxCode":"11",
        "taxDescription":"Private Light Goods (PLG)",
        "firstRegistration":"2007-03-30",
        "fleetNumber":"654321",
        "fuelType":"Petrol",
        "colour":"White",
        "licensingType":"L",
        "wheelplan": "2 Axle rigid body",
        "bodyType": "2 Door Saloon",
        "liability": "2014-01-01",
        "cylinderCapacity": 1600,
        "co2Emissions": 123,
        "massInService": 3500,
        "numberOfSeats": 5,
        "standingCapacity": 8,
        "revenueWeight": 2000,
        "firstRegistrationUk": "2007-03-30",
        "vehicleCategory": "M1",
        "motExpiry": "2010-03-30"
      }""")
    val vehicle = Vehicle(json)

    assert(vehicle.registrationNumber == "AB07 XYZ")
    assert(vehicle.make == "FORD")
    assert(vehicle.taxCode == "11")
    assert(vehicle.taxDescription == "Private Light Goods (PLG)")
    assert(vehicle.firstRegistration == DateTime.parse("2007-03-30").toDate)
    assert(vehicle.fleetNumber == "654321")
    assert(vehicle.fuelType == "Petrol")
    assert(vehicle.colour == "White")
    assert(vehicle.licensingType == LicensingType.LICENSED)
    assert(vehicle.co2Emissions == Some(123))
    assert(vehicle.cylinderCapacity == Some(1600))
    assert(vehicle.liability == Some(DateTime.parse("2014-01-01").toDate))
    assert(vehicle.taxBand == Some("D"))
    assert(vehicle.taxStatus == Some("Expired"))
    assert(vehicle.massInService == Some(3500))
    assert(vehicle.numberOfSeats == Some(5))
    assert(vehicle.standingCapacity == Some(8))
    assert(vehicle.revenueWeight == Some(2000))
    assert(vehicle.wheelplan == "2 Axle rigid body")
    assert(vehicle.firstRegistrationUk == Some(DateTime.parse("2007-03-30").toDate))
    assert(vehicle.vehicleCategory == Some("M1"))
    assert(vehicle.bodyType == "2 Door Saloon")
    assert(vehicle.motExpiry == Some(DateTime.parse("2010-03-30").toDate))
  }

  "Vehicle.apply(JsValue)" should "create a vehicle if required fields are supplied" in {
    val json = Json.parse(
      """{
        "registrationNumber": "AB07 XYZ",
        "make":"FORD",
        "taxCode":"11",
        "taxDescription":"Private Light Goods (PLG)",
        "firstRegistration":"2007-03-30",
        "fleetNumber":"654321",
        "fuelType":"Petrol",
        "colour":"Red",
        "licensingType":"L",
        "wheelplan": "2 Axle rigid body",
        "bodyType": "2 Door Saloon"
      }""")
    val vehicle = Vehicle(json)

    assert(vehicle.registrationNumber == "AB07 XYZ")
    assert(vehicle.make == "FORD")
    assert(vehicle.taxCode == "11")
    assert(vehicle.taxDescription == "Private Light Goods (PLG)")
    assert(vehicle.firstRegistration == DateTime.parse("2007-03-30").toDate)
    assert(vehicle.fleetNumber == "654321")
    assert(vehicle.fuelType == "Petrol")
    assert(vehicle.colour == "Red")
    assert(vehicle.licensingType == LicensingType.LICENSED)
    assert(vehicle.co2Emissions.isEmpty)
    assert(vehicle.cylinderCapacity.isEmpty)
    assert(vehicle.liability.isEmpty)
    assert(vehicle.taxBand.isEmpty)
    assert(vehicle.taxStatus.isEmpty)
  }

  "Vehicle.apply(JsValue)" should "throw Exception if JSON is empty" in {
    val json = Json.parse( """{}""")
    intercept[JsResultException] {
      Vehicle(json)
    }
  }

  "Vehicle.apply(JsValue)" should "throw Exception if a required field is not present" in {
    val json = Json.parse(
      """{
        "make":"FORD",
        "taxCode":"11",
        "taxDescription":"Private Light Goods (PLG)",
        "firstRegistration":"2007-03-30",
        "fleetNumber":"654321",
        "fuelType":"Petrol",
        "colour":"Red"
      }""")

    intercept[JsResultException] {
      Vehicle(json)
    }
  }
}
