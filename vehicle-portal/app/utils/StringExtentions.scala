package utils

import org.apache.commons.lang.WordUtils

object StringExtentions {

  class RichString(i: String) {
    def capitalizeFull =
      WordUtils.capitalizeFully(i, Array(' ', '-', '(', ')', '.', '+','/'))
        .replace(" And ", " and ")  // to cater for STOTHERT AND PITT
  }

  implicit def richString(i: String) = new RichString(i)

}

// Added to allow Intellij IDEA to find it
class StringExtentions
