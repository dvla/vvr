package forms

import org.scalatestplus.play._
import play.api.libs.json._
import play.api.test._
import play.api.test.Helpers._
import model.{VehicleRefinedSearchData, VehicleDetailsViewModel}
import uk.gov.dvla.domain.{FleetFilter, TaxStatus, LicensingType, Vehicle}
import java.util.Date
import org.joda.time.DateMidnight
import utils.files.DateFormatter
import java.text.SimpleDateFormat

class VehicleResponseFormSpec extends PlaySpec {

  private val registrationNumber = "DE67YTV"
  private val make = "nissan"
  private val makeExpected = "Nissan"
  private val liability = "2012-12-12"
  private val liabilityExpected = "12 December 2012"
  private val firstRegistration = "2013-02-09"
  private val firstRegistrationExpected = "9 February 2013"
  private val taxCode = "11"
  private val taxDescription = "Private Light Goods (PLG)"
  private val fleetNumber = "123456"
  private val taxStatus = TaxStatus.EXPIRED
  private val cylinderCapacity = 8000
  private val co2Emissions = 483
  private val taxBand = "M"
  private val fuelType = "Heavy Oil (Diesel)"
  private val colour = "yellow"
  private val colourExpected = "Yellow"
  private val licensingType = LicensingType.LICENSED
  private val massInService = 3500
  private val massInServiceExpected = "3,500"
  private val numberOfSeats = 5
  private val standingCapacity = 8
  private val revenueWeight = 44000
  private val revenueWeightExpected = "44,000"
  private val wheelplan = "2 Axle rigid body"
  private val firstRegistrationUk = "2013-02-09"
  private val firstRegistrationUkExpected = "9 February 2013"
  private val vehicleCategory = "M1"
  private val bodyType = "2 Door Saloon"
  private val nonM1VehicleCategory = "A1"
  private val model = "FaBiA"
  private val modelExpected = "Fabia"
  private val vin = "87E8E8JE8E7"
  private val lastV5 = "2011-05-12"
  private val lastV5Expected = "12 May 2011"
  private val engineNumber = "8S98J98E8J"

  "After providing legit vehicle details that retrieves the full set of data, the portal" must {
    running(FakeApplication()) {

      val response = Json.parse(
        s"""{
        "registrationNumber": "$registrationNumber",
        "make": "$make",
        "liability": "$liability",
        "firstRegistration": "$firstRegistration",
        "taxCode": "$taxCode",
        "taxDescription": "$taxDescription",
        "fleetNumber": "$fleetNumber",
        "cylinderCapacity": $cylinderCapacity,
        "co2Emissions": $co2Emissions,
        "fuelType": "$fuelType",
        "colour": "$colour",
        "licensingType": "$licensingType",
        "massInService": $massInService,
        "numberOfSeats": $numberOfSeats,
        "standingCapacity": $standingCapacity,
        "revenueWeight": $revenueWeight,
        "wheelplan": "$wheelplan",
        "firstRegistrationUk": "$firstRegistrationUk",
        "vehicleCategory": "$vehicleCategory",
        "bodyType": "$bodyType"
        } """)

      val vehicle = Vehicle(response)
      val viewModel = VehicleDetailsViewModel(vehicle)
      val viewString = views.html.searchResult(viewModel, 0, VehicleRefinedSearchData(Some(FleetFilter.ALL))).toString

      "show fleet number" in {
        viewString must include("Fleet reference number " + fleetNumber)
      }

      def testThisComesBeforeThat(thiz:String, that:String) {
        assert(viewString.indexOf(thiz) < viewString.indexOf(that), s"$thiz should come before $that")
      }

      val orderedFields = List("Registration number", "Vehicle make",
        "Date of first registration", "Date of first registration in the UK",
        "Tax class", "Cylinder capacity (cc)", "CO2 Emissions", "Fuel type", "Tax band",
        "Vehicle colour", "Body type", "Revenue weight (kg)", "Mass in service (kg)",
        "Number of seats (includes driver)", "Standing capacity", "Wheelplan",
        "Vehicle category")

      "display the fields in the correct order" in {
        for(i <- 1 until orderedFields.length) {
          testThisComesBeforeThat(orderedFields(i-1), orderedFields(i))
        }
      }

      testForExistence(viewString, "show registration number", "Registration number", registrationNumber)
      testForExistence(viewString, "show vehicle make", "Vehicle make", makeExpected)
      testForExistence(viewString, "show formatted date of first registration", "Date of first registration", firstRegistrationExpected)
      testForExistence(viewString, "show tax class", "Tax class", s"$taxCode - $taxDescription")
      testForExistence(viewString, "show cylinder capacity", "Cylinder capacity (cc)", cylinderCapacity)
      testForExistence(viewString, "show co2 emissions", "CO2 Emissions", co2Emissions)
      testForExistence(viewString, "show tax band", "Tax band", taxBand)
      testForExistence(viewString, "show fuel type", "Fuel type", fuelType)
      testForExistence(viewString, "show colour", "Vehicle colour", colourExpected)
      testForExistence(viewString, "show mass in service", "Mass in service (kg)", massInServiceExpected)
      testForExistence(viewString, "show number of seats", "Number of seats (includes driver)", numberOfSeats)
      testForExistence(viewString, "show standing capacity", "Standing capacity", standingCapacity)
      testForExistence(viewString, "show revenue weight", "Revenue weight (kg)", revenueWeightExpected)
      testForExistence(viewString, "show wheelplan", "Wheelplan", wheelplan)
      testForExistence(viewString, "show date of first reg uk", "Date of first registration in the UK", firstRegistrationUkExpected)
      testForExistence(viewString, "show vehicle category", "Vehicle category", vehicleCategory)
      testForExistence(viewString, "show body type", "Body type", bodyType)
    }
  }

  "After providing legit vehicle details that retrieves a subset of data, the portal" must {
    running(FakeApplication()) {

      val response = Json.parse(
        s"""{
        "registrationNumber": "$registrationNumber",
        "make": "$make",
        "taxCode": "$taxCode",
        "taxDescription": "$taxDescription",
        "liability": "$liability",
        "firstRegistration": "$firstRegistration",
        "fleetNumber": "$fleetNumber",
        "colour": "$colour",
        "licensingType": "$licensingType",
        "fuelType": "$fuelType",
        "wheelplan": "$wheelplan",
        "bodyType": "$bodyType"
        } """)

      val viewString = views.html.searchResult(VehicleDetailsViewModel(Vehicle(response)), 0, VehicleRefinedSearchData(Some(FleetFilter.ALL))).toString

      "show fleet number" in {
        viewString must include("Fleet reference number " + fleetNumber)
      }

      testForExistence(viewString, "show registration number", "Registration number", registrationNumber)
      testForExistence(viewString, "show vehicle make", "Vehicle make", makeExpected)
      testForExistence(viewString, "show formatted date of first registration", "Date of first registration", firstRegistrationExpected)
      testForExistence(viewString, "show tax class", "Tax class", s"$taxCode - $taxDescription")
      testForAbsence(viewString, "show cylinder capacity", "Cylinder capacity (cc)")
      testForAbsence(viewString, "show co2 emissions", "CO2 Emissions")
      testForAbsence(viewString, "show tax band", "Tax band")
      testForExistence(viewString, "show fuel type", "Fuel type", fuelType)
      testForExistence(viewString, "show colour", "Vehicle colour", colourExpected)
      testForExistence(viewString, "show wheelplan", "Wheelplan", wheelplan)
      testForExistence(viewString, "show body type", "Body type", bodyType)
      testForAbsence(viewString, "show mass in service", "Mass in service (kg)")
      testForAbsence(viewString, "show number of seats", "Number of seats (includes driver)")
      testForAbsence(viewString, "show standing capacity", "Standing capacity")
      testForAbsence(viewString, "show revenue weight", "Revenue weight (kg)")
      testForAbsence(viewString, "show date of first reg uk", "Date of first registration in the UK")
      testForAbsence(viewString, "show vehicle category", "Vehicle category")
    }
  }

  "After providing legit vehicle details that retrieves the minimum of data containing 'blanks and zeros', the portal" must {
    running(FakeApplication()) {
      val response = Json.parse(
        s"""{
        "registrationNumber": "$registrationNumber",
        "make": "$make",
        "firstRegistration": "$firstRegistration",
        "taxCode": "$taxCode",
        "taxDescription": "$taxDescription",
        "fleetNumber": "$fleetNumber",
        "fuelType": "$fuelType",
        "colour": "$colour",
        "licensingType": "$licensingType",
        "wheelplan": "$wheelplan",
        "firstRegistrationUk": "$firstRegistrationUk",
        "bodyType": "$bodyType",
        "liability": "",
        "cylinderCapacity": 0,
        "massInService": 0,
        "standingCapacity": 0,
        "revenueWeight": 0
        } """)

      /*
        These are the 'blanks' mentioned in the test description achieved by simply omitting them from the JSON
        "vehicleCategory": "",
        "co2Emissions": 0
        "numberOfSeats": 0,
      */

      val viewString = views.html.searchResult(VehicleDetailsViewModel(Vehicle(response)), 0, VehicleRefinedSearchData(Some(FleetFilter.ALL))).toString

      "show fleet number" in {
        viewString must include("Fleet reference number " + fleetNumber)
      }

      testForExistence(viewString, "show registration number", "Registration number", registrationNumber)
      testForExistence(viewString, "show vehicle make", "Vehicle make", makeExpected)
      // testForExistence(viewString, "show formatted date of liability", "Date of liability", "")
      testForExistence(viewString, "show formatted date of first registration", "Date of first registration", firstRegistrationExpected)
      testForExistence(viewString, "show tax class", "Tax class", s"$taxCode - $taxDescription")
      testForExistence(viewString, "show fuel type", "Fuel type", fuelType)
      testForExistence(viewString, "show colour", "Vehicle colour", colourExpected)
      testForExistence(viewString, "show wheelplan", "Wheelplan", wheelplan)
      testForExistence(viewString, "show date of first reg uk", "Date of first registration in the UK", firstRegistrationUkExpected)
      testForExistence(viewString, "show body type", "Body type", bodyType)

      testForAbsence(viewString, "show tax status", "Tax status")
      testForAbsence(viewString, "show tax band", "Tax band")
      testForAbsence(viewString, "show vehicle category", "Vehicle category")
      testForAbsence(viewString, "show cylinder capacity", "Cylinder capacity (cc)")
      testForAbsence(viewString, "show co2 emissions", "CO2 Emissions")
      testForAbsence(viewString, "show mass in service", "Mass in service (kg)")
      testForAbsence(viewString, "show number of seats", "Number of seats (includes driver)")
      testForAbsence(viewString, "show standing capacity", "Standing capacity")
      testForAbsence(viewString, "show revenue weight", "Revenue weight (kg)")
    }
  }

  "Not to Display Tax Band when Vehicle category is not M1" must {
    running(FakeApplication()) {

      val response = Json.parse(
        s"""{
        "registrationNumber": "$registrationNumber",
        "make": "$make",
        "taxCode": "$taxCode",
        "taxDescription": "$taxDescription",
        "firstRegistration": "$firstRegistration",
        "fleetNumber": "$fleetNumber",
        "colour": "$colour",
        "licensingType": "$licensingType",
        "fuelType": "$fuelType",
        "wheelplan": "$wheelplan",
        "bodyType": "$bodyType"} """)

      val viewString = views.html.searchResult(VehicleDetailsViewModel(Vehicle(response)), 0, VehicleRefinedSearchData(Some(FleetFilter.ALL))).toString
      testForAbsence(viewString, "show tax band", "Tax band")
    }

  }

  "Check for presence/absence of optional US1121 fields (values not set)" must {
    running(FakeApplication()) {

      val response = Json.parse(
        s"""{
        "registrationNumber": "$registrationNumber",
        "make": "$make",
        "taxCode": "$taxCode",
        "taxDescription": "$taxDescription",
        "firstRegistration": "$firstRegistration",
        "fleetNumber": "$fleetNumber",
        "colour": "$colour",
        "licensingType": "$licensingType",
        "fuelType": "$fuelType",
        "wheelplan": "$wheelplan",
        "bodyType": "$bodyType"} """)

      val viewString = views.html.searchResult(VehicleDetailsViewModel(Vehicle(response)), 0, VehicleRefinedSearchData.emptyFilter).toString
      testForExistence(viewString, "show model", "Model", "")
      testForAbsence(viewString, "show vin", "VIN/Chassis/Frame No.")
      testForAbsence(viewString, "show last v5", "Date of last V5C issue")
      testForExistence(viewString, "show engine number", "Engine number", "")
    }
  }

  "Check for presence of optional US1121 fields (values set)" must {
    running(FakeApplication()) {

      val response = Json.parse(
        s"""{
      "registrationNumber": "$registrationNumber",
      "make": "$make",
      "taxCode": "$taxCode",
      "taxDescription": "$taxDescription",
      "firstRegistration": "$firstRegistration",
      "fleetNumber": "$fleetNumber",
      "colour": "$colour",
      "licensingType": "$licensingType",
      "fuelType": "$fuelType",
      "wheelplan": "$wheelplan",
      "bodyType": "$bodyType",
      "model": "$model",
      "vin": "$vin",
      "lastV5": "$lastV5",
      "engineNumber": "$engineNumber"} """)

      val viewString = views.html.searchResult(VehicleDetailsViewModel(Vehicle(response)), 0, VehicleRefinedSearchData.emptyFilter).toString
      testForExistence(viewString, "show model", "Model", modelExpected)
      testForExistence(viewString, "show vin", "VIN/Chassis/Frame No.", vin)
      testForExistence(viewString, "show last v5", "Date of last V5C issue", lastV5Expected)
      testForExistence(viewString, "show engine number", "Engine number", engineNumber)
    }
  }

  "Display various Tax Alerts based on Tax Status" must {
    running(FakeApplication()) {

      testForAlertExistence(buildViewString(buildJsonResponseByTaxStatus(Some(new DateMidnight().plusMonths(2).toDate), LicensingType.LICENSED)), "Taxed", "&#10004")
      testForAlertExistence(buildViewString(buildJsonResponseByTaxStatus(Some(new DateMidnight().plusDays(7).toDate), LicensingType.LICENSED)), "Tax Due", "&#10004")
      testForAlertExistence(buildViewString(buildJsonResponseByTaxStatus(Some(new DateMidnight().minusDays(7).toDate), LicensingType.LICENSED)), "Untaxed", "&#10008")
      testForAlertExistence(buildViewString(buildJsonResponseByTaxStatus(None, LicensingType.SORN)), "SORN", "&#10004")
    }

  }


  def buildViewString(response: JsValue): String = {
    views.html.searchResult(VehicleDetailsViewModel(Vehicle(response)), 0, VehicleRefinedSearchData(Some(FleetFilter.ALL))).toString
  }

  private def buildJsonResponseByTaxStatus(liability: Option[Date], licensingType: String): JsValue= {
    val formatter = new SimpleDateFormat("yyyy-MM-dd");
    val liabiltyString: String = liability.map(formatter.format(_)).getOrElse("")
    Json.parse(
      s"""{
        "registrationNumber": "$registrationNumber",
        "make": "$make",
        "taxCode": "$taxCode",
        "taxDescription": "$taxDescription",
        "firstRegistration": "$firstRegistration",
        "fleetNumber": "$fleetNumber",
        "liability": "$liabiltyString",
        "colour": "$colour",
        "licensingType": "$licensingType",
        "fuelType": "$fuelType",
        "wheelplan": "$wheelplan",
        "vehicleCategory": "$nonM1VehicleCategory",
        "bodyType": "$bodyType"} """)

  }

  private def testForExistence(viewString: String, description: String, label: String, value: Any) = {
    description in {
      viewString must include("<li><span>" + label + "</span><strong>" + value + "</strong></li>")
    }
  }

  private def testForAbsence(viewString: String, description: String, label: String) = {
    s"not $description" in {
      viewString must not include(label)
    }
  }

  private def testForAlertExistence(viewString: String, alertText: String, tickCrossCode: String) = {
    s"Show $alertText and tick code $tickCrossCode" in {
      viewString must include("<h2 class=\"bold-medium\"> "+tickCrossCode+"; " + alertText +"</h2>")
    }
  }

}