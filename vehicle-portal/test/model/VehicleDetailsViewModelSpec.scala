package model

import uk.gov.dvla.domain.{TaxStatus, LicensingType, Vehicle}
import java.util.Date
import org.scalatest.{GivenWhenThen, FlatSpec}

class VehicleDetailsViewModelSpec extends FlatSpec  with GivenWhenThen {

  val _2000_09_23 = new Date(969663600000L)

  val vehicleDvmWithSorn = VehicleDetailsViewModel(new Vehicle(
    "BT54KNK", "Ford", _2000_09_23, "000000", "A", "PLG", "3", "Blue", "2 Axle rigid body", "Saloon",
    LicensingType.SORN,
    None, Some(1), Some(2)))


  it should "be able to return proper values for singleView" in {
    Given("licensingType = licensingType.SORN")

    When("geting liability date")
    Then("it should be empty")
    assert(vehicleDvmWithSorn.liability.isEmpty)

    When("geting tax status")
    Then("it should return SORN")
    assert(vehicleDvmWithSorn.taxStatus.get == TaxStatus.SORN)


    Given("licensingType = licensingType.LICENSED and licensed date = '2009-09-23'")
    val vehicleDvmWithoutSorn = VehicleDetailsViewModel(new Vehicle(
      "BT54KNK", "Ford", _2000_09_23, "000000", "A", "PLG", "3", "Blue", "2 Axle rigid body", "Saloon",
      LicensingType.LICENSED,
      Some(_2000_09_23),
      Some(1), Some(2)))

    When("geting liability date")
    Then("it should be 23 September 2000")
    assert(vehicleDvmWithoutSorn.liability == Some("23 September 2000"))

    When("geting tax status")
    Then("it should return Expired")
    assert(vehicleDvmWithoutSorn.taxStatus.get == TaxStatus.EXPIRED)
  }
}
