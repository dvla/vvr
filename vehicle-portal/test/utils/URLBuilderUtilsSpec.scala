package utils

import org.scalatest.{Matchers, FlatSpec}


class URLBuilderUtilsSpec extends FlatSpec with Matchers {

  "No query params" should "return an empty string" in {
    URLBuilderUtils.buildQueryParams(Map()) should be("")
  }

  "Map with params with None values" should "return an empty string" in {
    URLBuilderUtils.buildQueryParams(Map("vehicleMakeFleet" -> None, "registrationNumberFleet" -> None)) should be("")
  }

  "Map with params with Some values" should "valid string with query parameters" in {
    URLBuilderUtils.buildQueryParams(Map("vehicleMakeFleet" -> Some("FORD"),
                                        "registrationNumberFleet" -> None,
                                        "taxStatus" -> Some("paid"))) should be("?vehicleMakeFleet=FORD&taxStatus=paid")
  }

}
