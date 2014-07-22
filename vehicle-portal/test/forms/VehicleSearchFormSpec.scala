package forms

import org.scalatest.{Matchers, FlatSpec}
import play.api.data.FormError


class VehicleSearchFormSpec extends FlatSpec with Matchers {

  object VehicleSearchForm extends VehicleSearchForm

  private val emptyFleetNumber = Map("fleetNumber" -> "", "registrationNumber" -> "QERTY", "vehicleMake" -> "Mazda")
  private val emptyRegistrationNumber = Map("fleetNumber" -> "123456", "registrationNumber" -> "", "vehicleMake" -> "Mazda")
  private val emptyVehicleMake = Map("fleetNumber" -> "123456", "registrationNumber" -> "QERTY", "vehicleMake" -> "")

  private val fleetNumberWithDashInTheMiddle = Map("fleetNumber" -> "123-45", "registrationNumber" -> "QERTY", "vehicleMake" -> "Mazda")
  private val fleetNumberWithLetter = Map("fleetNumber" -> "2A3456", "registrationNumber" -> "QERTY", "vehicleMake" -> "Mazda")
  private val fleetNumberWithFiveNumbers = Map("fleetNumber" -> "23456", "registrationNumber" -> "QERTY", "vehicleMake" -> "Mazda")

  private val notAlphaNumericRegistrationNumber = Map("fleetNumber" -> "123456", "registrationNumber" -> "&123TT", "vehicleMake" -> "Mazda")
  private val oneCharacterRegistrationNumber = Map("fleetNumber" -> "123456", "registrationNumber" -> "W", "vehicleMake" -> "Mazda")
  private val eightCharactersRegistrationNumber = Map("fleetNumber" -> "123456", "registrationNumber" -> "WERTYU88", "vehicleMake" -> "Mazda")

  private val correctlyCompletedForm = Map("fleetNumber" -> "12345-", "registrationNumber" -> "Q9", "vehicleMake" -> "Mazda")

  "Empty fleet number field" should "prevent from binding to vehicleForm" in {
    assert(bindingErrors(emptyFleetNumber).exists(errorWithMessage(_, "fleetNumber", "vvrform.enter.your.fleet.number")))
  }

  "Empty registration number field" should "prevent from binding to vehicleForm" in {
    assert(bindingErrors(emptyRegistrationNumber).exists(errorWithMessage(_, "registrationNumber", "vvrform.enter.your.vehicle.registration.number")))
  }

  "Empty vehicle make field" should "prevent from binding to vehicleForm" in {
    assert(bindingErrors(emptyVehicleMake).exists(errorWithMessage(_, "vehicleMake", "vvrform.enter.your.vehicle.make")))
  }

  "Fleet number with a dash in the middle" should "prevent from binding to vehicleForm" in {
    assert(bindingErrors(fleetNumberWithDashInTheMiddle).exists(errorWithMessage(_, "fleetNumber", "vvrform.enter.a.valid.fleet.number")))
  }

  "Fleet number with a letter" should "prevent from binding to vehicleForm" in {
    assert(bindingErrors(fleetNumberWithLetter).exists(errorWithMessage(_, "fleetNumber", "vvrform.enter.a.valid.fleet.number")))
  }

  "Fleet number built from five numbers" should "prevent from binding to vehicleForm" in {
    assert(bindingErrors(fleetNumberWithFiveNumbers).exists(errorWithMessage(_, "fleetNumber", "vvrform.enter.a.valid.fleet.number")))
  }

  "Not alphanumeric character in registration number field" should "prevent from binding to vehicleForm" in {
    assert(bindingErrors(notAlphaNumericRegistrationNumber).exists(errorWithMessage(_, "registrationNumber", "vvrform.enter.a.valid.vehicle.registration.number")))
  }

  "One character registration number" should "prevent from binding to vehicleForm" in {
    assert(bindingErrors(oneCharacterRegistrationNumber).exists(errorWithMessage(_, "registrationNumber", "vvrform.enter.a.valid.vehicle.registration.number")))
  }

  "Eight characters registration number" should "prevent from binding to vehicleForm" in {
    assert(bindingErrors(eightCharactersRegistrationNumber).exists(errorWithMessage(_, "registrationNumber", "vvrform.enter.a.valid.vehicle.registration.number")))
  }

  "Correctly completed form" should "not produce bindings errors" in {
    bindingErrors(correctlyCompletedForm).size should be (0)
  }

  private def bindingErrors(fields: Map[String, String]) = {
    VehicleSearchForm.vehicleForm.bind(fields).errors
  }

  private def errorWithMessage(p: FormError, expectedField: String, expectedMessage: String): Boolean = {
    p.key == expectedField && p.message == expectedMessage
  }

}
