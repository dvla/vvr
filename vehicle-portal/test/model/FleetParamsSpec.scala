package model

import org.scalatest.{Matchers, FlatSpec}
import forms.VehicleRefinedSearchForm
import forms.VehicleRefinedSearchForm._

class FleetParamsSpec extends FlatSpec with Matchers with VehicleRefinedSearchForm {

  val goodForm = vehicleRefinedSearchForm.bind(Map(REGISTRATION_PARAM -> "AAA123A", MAKE_PARAM -> "FORD", MOT_MONTH_FROM_PARAM -> "4", MOT_YEAR_FROM_PARAM -> "2014"))
  val badForm = vehicleRefinedSearchForm.bind(Map(REGISTRATION_PARAM -> "A", MAKE_PARAM -> "FORD", MOT_MONTH_FROM_PARAM -> "x", MOT_YEAR_FROM_PARAM -> "2014"))

  "FleetParams" should "return the decoded form object" in {
    val vm = new FleetParams(goodForm)

    vm.data.registrationNumber should be (Some("AAA123A"))
    vm.data.vehicleMake should be (Some("FORD"))
    vm.data.mot should be (MOTExpiryFilter(Some(4), Some(2014)))
  }

  "data" should "represent the valid data from the form/query, ignoring error fields" in {
    val vm = new FleetParams(badForm)

    vm.data.registrationNumber should be (None)
    vm.data.vehicleMake should be (Some("FORD"))
    vm.data.mot should be (MOTExpiryFilter())  // all mot fields are ignored if one is invalid
  }

}
