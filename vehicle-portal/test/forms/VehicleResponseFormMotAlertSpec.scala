package forms

import java.util.Calendar

import common.PortalDateFormatter
import model.{VehicleDetailsViewModel, VehicleRefinedSearchData}
import org.scalatestplus.play._
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._
import uk.gov.dvla.domain.{FleetFilter, Vehicle}

class VehicleResponseFormMotAlertSpec extends PlaySpec {

  private val commonJson = s"""
        "registrationNumber": "CU63PYG",
        "make": "SKODA",
        "taxCode": "11",
        "taxDescription": "Private Light Goods (PLG)",
        "firstRegistration": "2013-02-09",
        "fleetNumber": "123456",
        "colour": "yellow",
        "licensingType": "L",
        "fuelType": "Heavy Oil (Diesel)",
        "taxBand": "M",
        "wheelplan": "2 Axle rigid body",
        "vehicleCategory": "M1",
        "bodyType": "2 Door Saloon",
        "motExpiry":"""

  /**
   * Helper method to reduce line length
   */
  private def getViewModel(json: String) = VehicleDetailsViewModel(Vehicle(Json.parse(json)))

  /**
   * Helper method to reduce line length
   */
  private def getMotExpiryForBusinessLogic(calendar: Calendar) = PortalDateFormatter.yyyyMMddWithHyphens(calendar.getTime)

  "Validate mot alert when expiry date was yesterday" must {
    running(FakeApplication()) {

      val motExpiry = {
        val cal = Calendar.getInstance()
        cal.roll(Calendar.DATE, -1)
        cal
      }

      val motExpiryForBusinessLogic = getMotExpiryForBusinessLogic(motExpiry)

      val viewModel = getViewModel(s"""{$commonJson "$motExpiryForBusinessLogic"}""")
      val viewString = views.html.searchResult(viewModel, 0, VehicleRefinedSearchData(Some(FleetFilter.ALL))).toString

      "be coloured red" in {
        viewString must include( """<div id="mot-alert-colour" class="alert danger-alert" >""")
      }
      "display a cross" in {
        viewString must include("""<h2 id="mot-alert-title" class="bold-medium">&#10008; MOT</h2>""")
      }
      "must state that expiry was yesterday" in {
        val motExpiryForView = PortalDateFormatter.format(motExpiry.getTime)
        viewString must include(s"""<p id="mot-alert-message" class="bold-xsmall">Expired: $motExpiryForView</p>""")
      }
    }
  }

  "Validate mot alert when expiry date is today" must {
    running(FakeApplication()) {

      val motExpiry = {
        Calendar.getInstance()
      }

      val motExpiryForBusinessLogic = getMotExpiryForBusinessLogic(motExpiry)

      val viewString = views.html.searchResult(getViewModel(s"""{$commonJson "$motExpiryForBusinessLogic"}"""), 0, VehicleRefinedSearchData(Some(FleetFilter.ALL))).toString

      "be coloured green" in {
        viewString must include( """<div id="mot-alert-colour" class="alert success-alert" >""")
      }
      "display a tick" in {
        viewString must include("""<h2 id="mot-alert-title" class="bold-medium">&#10004; MOT</h2>""")
      }
      "must state that expiry is today" in {
        val motExpiryForView = PortalDateFormatter.format(motExpiry.getTime)
        viewString must include(s"""<p id="mot-alert-message" class="bold-xsmall">Expires: $motExpiryForView</p>""")
      }
    }
  }

  "Validate mot alert when expiry date is tomorrow" must {
    running(FakeApplication()) {

      val motExpiry = {
        val cal = Calendar.getInstance()
        cal.roll(Calendar.DATE, 1)
        cal
      }

      val motExpiryForBusinessLogic = getMotExpiryForBusinessLogic(motExpiry)

      val viewString = views.html.searchResult(getViewModel(s"""{$commonJson "$motExpiryForBusinessLogic"}"""), 0, VehicleRefinedSearchData(Some(FleetFilter.ALL))).toString

      "be coloured green" in {
        viewString must include( """<div id="mot-alert-colour" class="alert success-alert" >""")
      }
      "display a tick" in {
        viewString must include("""<h2 id="mot-alert-title" class="bold-medium">&#10004; MOT</h2>""")
      }
      "must state that expiry is tomorrow" in {
        val motExpiryForView = PortalDateFormatter.format(motExpiry.getTime)
        viewString must include(s"""<p id="mot-alert-message" class="bold-xsmall">Expires: $motExpiryForView</p>""")
      }
    }
  }

  "Validate mot alert when expiry date is not on record" must {
    running(FakeApplication()) {

      val motExpiryForBusinessLogic = ""

      val viewString = views.html.searchResult(getViewModel(s"""{$commonJson "$motExpiryForBusinessLogic"}"""), 0, VehicleRefinedSearchData(Some(FleetFilter.ALL))).toString

      "be coloured red" in {
        viewString must include( """<div id="mot-alert-colour" class="alert danger-alert" >""")
      }
      "display a cross" in {
        viewString must include("""<h2 id="mot-alert-title" class="bold-medium">&#10008; MOT</h2>""")
      }
      "must state that expiry date is not on record" in {
        viewString must include(s"""<p id="mot-alert-message" class="bold-xsmall">No details held by DVLA</p>""")
      }
    }
  }
}