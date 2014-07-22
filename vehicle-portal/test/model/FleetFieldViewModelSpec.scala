package model

import org.scalatest.{Matchers, FlatSpec}
import play.api.data.{FormError, Field}
import forms.VehicleRefinedSearchForm

class FleetFieldViewModelSpec extends FlatSpec with Matchers with VehicleRefinedSearchForm {

  val dummyField = Field(vehicleRefinedSearchForm, "dummyField", Seq(), None, Seq(FormError("dummyField", "not a valid value")), Some("some value"))

  "FleetFieldViewModel" should "return the field properties" in {
    val vm = new FleetFieldViewModel(dummyField, "my label", Nil)

    vm.name should be ("dummyField")
    vm.labelName should be ("my label")
    vm.value should be (Some("some value"))
    vm.errors should be (Seq(FormError("dummyField", "not a valid value")))
  }

  "isChecked" should "return empty string if field name is invalid" in {
    val vm = new FleetFieldViewModel(dummyField, "my label", Nil)

    vm.isChecked("") should be ("")
  }

  "isChecked" should "return 'checked' if sub-field has a value" in {
    val form = vehicleRefinedSearchForm.bind(Map("mot.monthFrom" -> "4", "mot.expired" -> "true"))
    val vm = new FleetFieldViewModel(form("mot"), "mot", List("expired"))

    vm.isChecked("expired") should be ("checked")
  }

  "isChecked" should "return empty string if sub-field doesn't have a value" in {
    val form = vehicleRefinedSearchForm.bind(Map("mot.monthFrom" -> "4"))
    val vm = new FleetFieldViewModel(form("mot"), "mot", List("expired"))

    vm.isChecked("expired") should be ("")
  }

  "dateFieldValue" should "return padded month if it is a month" in {
    val form = vehicleRefinedSearchForm.bind(Map("mot.monthFrom" -> "4", "mot.expired" -> "true"))
    val vm = new FleetFieldViewModel(form("mot"), "mot", List("expired"))

    vm.dateFieldValue("monthFrom", DateInputType.MONTH) should be (Some("04"))
  }

  "dateFieldValue" should "return unpadded value if it is not specified as a month" in {
    val form = vehicleRefinedSearchForm.bind(Map("mot.monthFrom" -> "4", "mot.expired" -> "true"))
    val vm = new FleetFieldViewModel(form("mot"), "mot", List("expired"))

    vm.dateFieldValue("monthFrom", DateInputType.YEAR) should be (Some("4"))
  }

  "dateFieldValue" should "return unpadded value if field has an error" in {
    // invalid number value "q" will cause error in bind()
    val form = vehicleRefinedSearchForm.bind(Map("mot.monthFrom" -> "q", "mot.expired" -> "true"))
    val vm = new FleetFieldViewModel(form("mot"), "mot", List("expired"))

    vm.dateFieldValue("monthFrom", DateInputType.MONTH) should be (Some("q"))
  }

  "dateFieldValue" should "return None if field is empty" in {
    val form = vehicleRefinedSearchForm.bind(Map("mot.monthFrom" -> "4", "mot.expired" -> "true"))
    val vm = new FleetFieldViewModel(form("mot"), "mot", List("expired"))

    vm.dateFieldValue("monthTo", DateInputType.MONTH) should be (None)
  }

}
