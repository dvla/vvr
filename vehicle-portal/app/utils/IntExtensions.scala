package utils

import java.text.NumberFormat
import java.util.Locale

object IntExtensions {

  class DecimalFormat(value: Int) {
    val formatter = NumberFormat.getNumberInstance(Locale.UK)

    def formatUK = formatter.format(value)
  }

  implicit def decimalFormat(value: Int) = new DecimalFormat(value)
}
