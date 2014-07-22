package utils.files

import org.scalatest.{Matchers, FlatSpec}
import java.util.Date


class FormatterSpec extends FlatSpec with Matchers {

  "DateFormatter.apply" should "convert a date into 'yyyy-MM-dd' formated string" in {
    val _2000_09_23 = 969663600000L

    DateFormatter.apply(new Date(_2000_09_23)) should be("2000-09-23")
  }

  "QuoteSurroundFormatter.apply" should "surround a string with quotes" in {
    val expectedResult = "\"Yes, I'm surrounded.\""

    QuoteSurroundFormatter.apply("Yes, I'm surrounded.") should be(expectedResult)
  }

}
