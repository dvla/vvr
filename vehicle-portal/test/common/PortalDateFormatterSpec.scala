package common

import org.scalatest.{Matchers, FlatSpec}
import java.util.Calendar

class PortalDateFormatterSpec extends FlatSpec with Matchers {
  "Formatting a date" should "return string in the format 'd MMMM yyyy'" in {
    val expectedResult = "9 December 2014"
    val cal = Calendar.getInstance
    cal.set(2014, 11, 9)
    val result = PortalDateFormatter.format(cal.getTime)
    assert(result == expectedResult)
  }

  it should "return an empty string when no date is provided" in {
    val expectedResult = ""
    val result = PortalDateFormatter.format(null)
    assert(result == expectedResult)
  }

  "yyyyMMdd formatting a date" should "return string in the format 'yyyyMMdd'" in {
    val expectedResult = "20141209"
    val cal = Calendar.getInstance
    cal.set(2014, 11, 9)

    PortalDateFormatter.yyyyMMdd(cal.getTime) should be(expectedResult)
  }

  "HHmmss formatting a date" should "return string in the format 'HHmmss'" in {
    val expectedResult = "145223"
    val cal = Calendar.getInstance
    cal.set(2014, 11, 9, 14, 52, 23)

    PortalDateFormatter.HHmmss(cal.getTime) should be(expectedResult)
  }

  "formatMonth(1)" should "return 01" in {
    PortalDateFormatter.formatMonth("1") should be ("01")
  }

  "formatMonth(10)" should "return 10" in {
    PortalDateFormatter.formatMonth("10") should be ("10")
  }

}
