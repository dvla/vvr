package model
import org.scalatest._
import uk.gov.dvla.domain.{TaxStatus, LicensingType, Vehicle}
import java.util.Date

/**
 * General testing VehicleDetailsViewModel methods
 */
class VehicleListExportViewModelSpec extends FlatSpec  with GivenWhenThen {

  val _2000_09_23: Long = 969663600000L
  val vehicleLevmWithSorn = VehicleListExportViewModel(
    new Vehicle("BT54KNK",
            "Ford",
            new Date(_2000_09_23),
            "000000",
            "A",
            "PLG",
            "3",
            "Blue",
            "",
            "",
            LicensingType.SORN,
            None,
            Some(1),
            Some(2),
            motExpiry = Some(new Date(_2000_09_23))
    )
  )

  "VehicleListExportViewModel" should "be able to return proper values for multiView export" in {
    Given("taxState is SORN")

    When("getting liability date")
    Then("it should return N/A")
    assert(vehicleLevmWithSorn.liability.get == "N/A")

    When("getting tax state")
    Then("it should return SORN")
    assert(vehicleLevmWithSorn.taxStatus.get == TaxStatus.SORN)
  }
}
