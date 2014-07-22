package model

import org.scalatest.{FlatSpec, Matchers}
import uk.gov.dvla.domain.FleetFilter


class VehicleRefinedSearchDataSpec extends FlatSpec with Matchers {

  "flattenToServiceQueryParams" should "return a map with query string params" in {
    VehicleRefinedSearchData(Some(FleetFilter.ALL), Some("A9"), None, MOTExpiryFilter(Some(1), Some(2000), None, None, None, None))
      .flattenToServiceQueryParams should be (Map("filter" -> "all", "registrationNumber" -> "A9", "motMonthFrom" -> "1", "motYearFrom" -> "2000"))
  }

  "flattenToServiceQueryParams with no filters" should "return an empty map" in {
    VehicleRefinedSearchData(None, None, None, MOTExpiryFilter(None, None, None, None, None, None))
      .flattenToServiceQueryParams should be (Map())
  }

  "flattenToServiceQueryParams with one filter" should "return a map with one value" in {
    VehicleRefinedSearchData(None, None, None, MOTExpiryFilter(Some(1), None, None, None, None, None))
      .flattenToServiceQueryParams should be (Map("motMonthFrom" -> "1"))
  }

  "isRefined" should "return false if there are no query params" in {
    VehicleRefinedSearchData(None, None, None, MOTExpiryFilter()).isRefined should be (false)
  }

  "isRefined" should "return true if there are some refined query params" in {
    VehicleRefinedSearchData(None, Some("A9"), None, MOTExpiryFilter()).isRefined should be (true)
  }

  "isRefined" should "return false if there is only an alert filter" in {
    VehicleRefinedSearchData(Some(FleetFilter.DUE), None, None, MOTExpiryFilter()).isRefined should be (false)
  }
}
