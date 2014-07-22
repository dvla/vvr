package forms

import org.scalatest.{Matchers, FlatSpec}
import model.MOTExpiryFilter


class VehicleRefinedSearchFormSpec extends FlatSpec with Matchers {

  "1/2012 to 1/2012 " should "be a valid date range" in {
    val motExpiry = MOTExpiryFilter(Some(1), Some(2012), Some(1), Some(2012), None, None)

    VehicleRefinedSearchForm.validMOTExpiryFilterDateRange(motExpiry) should be (true)
  }

  "2/2012 to 1/2012 " should "not be a valid date range" in {
    val motExpiry = MOTExpiryFilter(Some(2), Some(2012), Some(1), Some(2012), None, None)

    VehicleRefinedSearchForm.validMOTExpiryFilterDateRange(motExpiry) should be (false)
  }

  "2/2012 to empty date to " should "be a valid date range" in {
    val motExpiry = MOTExpiryFilter(Some(2), Some(2012), None, None, None, None)

    VehicleRefinedSearchForm.validMOTExpiryFilterDateRange(motExpiry) should be (true)
  }

  "2/2012 to 1/ " should "not be a valid date range" in {
    val motExpiry = MOTExpiryFilter(Some(2), Some(2012), Some(1), None, None, None)

    VehicleRefinedSearchForm.validMOTExpiryFilterDateRange(motExpiry) should be (false)
  }

  "2/2012 to /2012" should "not be a valid date range" in {
    val motExpiry = MOTExpiryFilter(Some(2), Some(2012), None, Some(2012), None, None)

    VehicleRefinedSearchForm.validMOTExpiryFilterDateRange(motExpiry) should be (false)
  }

  "Empty mot filter" should "be a valid date range" in {
    val motExpiry = MOTExpiryFilter(None, None, None, None, None, None)

    VehicleRefinedSearchForm.validMOTExpiryFilterDateRange(motExpiry) should be (true)
  }

  "Empty date range with expired or no date held option" should "be a valid mot" in {
    val motExpiry = MOTExpiryFilter(None, None, None, None, Some(true), None)

    VehicleRefinedSearchForm.validMOTExpiryFilterDateRange(motExpiry) should be (true)
  }

}
