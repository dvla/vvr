package utils

import org.scalatest.{MustMatchers, WordSpec, Matchers, FlatSpec}
import play.api.data.FormError


class StringExtentionsSpec extends WordSpec with MustMatchers {

  import utils.StringExtentions._

  "StringExtentions" must {
    "capitalize properly 'MERCEDES-BENZ' to 'Mercedes-Benz'" in {
      "MERCEDES-BENZ".capitalizeFull must be("Mercedes-Benz")
    }

    "capitalize properly 'niesmann + bischoff' to 'Niesmann + Bischoff'" in {
      "niesmann + bischoff".capitalizeFull must be("Niesmann + Bischoff")
    }

    "capitalize properly 'SEDDON/ATKINSON' to 'Seddon/Atkinson'" in {
      "SEDDON/ATKINSON".capitalizeFull must be("Seddon/Atkinson")
    }

    "capitalize properly 'STOTHERT AND PITT' to 'Stothert and Pitt'" in {
      "STOTHERT AND PITT".capitalizeFull must be("Stothert and Pitt")
    }

    "capitalize properly 'CROMPTON (LEYLAND)' to 'Crompton (Leyland)''" in {
      "CROMPTON (LEYLAND)'".capitalizeFull must be("Crompton (Leyland)'")
    }
  }

}
