package model

import org.scalatest._
import uk.gov.dvla.domain.{TaxStatus, LicensingType, Vehicle}
import java.util.Date

/**
 * General testing VehicleDetailsViewModel methods
 */
class VehicleListViewModelSpec extends FlatSpec  with GivenWhenThen {

  val _2000_09_23: Long = 969663600000L
  val vehicleDvmWithSorn = VehicleListViewModel(new Vehicle(
    "BT54KNK", "Ford", new Date(_2000_09_23), "000000", "A", "PLG", "3", "Blue", "", "", LicensingType.SORN,
    None, None, None, None, Some(1), Some(2)))

  it should "be able to return proper values for multiView" in {
    Given("taxState is SORN")

    When("geting liability date")
    Then("it should return SORN")
    assert(vehicleDvmWithSorn.liability.get == TaxStatus.SORN)

  }

}
