package uk.gov.dvla.utils

import org.scalatest.{Matchers, FlatSpec}


class RegistrationNumberSpec extends FlatSpec with Matchers {

  /*
    Valid registration number formats (A - character, 9 - digit) :

      A9
      A99
      A999
      A9999
      AA9
      AA99
      AA999
      AA9999
      AAA9
      AAA99
      AAA999
      AAA9999
      AAA9A
      AAA99A
      AAA999A
      9A
      9AA
      9AAA
      99A
      99AA
      99AAA
      999A
      999AA
      999AAA
      9999A
      9999AA
      A9AAA
      A99AAA
      A999AAA
      AA99AAA
    */

  "A9" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("Z8") should be(true)
  }

  "A99" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("Z82") should be(true)
  }

  "A999" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("Z824") should be(true)
  }

  "A9999" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("Z8234") should be(true)
  }

  "AA9" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("DZ8") should be(true)
  }

  "AA99" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("DZ87") should be(true)
  }

  "AA999" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("DZ867") should be(true)
  }

  "AA9999" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("DZ8876") should be(true)
  }

  "AAA9" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("DZA8") should be(true)
  }

  "AAA99" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("DAZ87") should be(true)
  }

  "AAA999" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("DZK890") should be(true)
  }

  "AAA9999" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("DZQ8980") should be(true)
  }

  "AAA9A" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("DZA8Q") should be(true)
  }

  "AAA99A" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("DZH87A") should be(true)
  }

  "AAA999A" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("DZH876A") should be(true)
  }

  "9A" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("6A") should be(true)
  }

  "9AA" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("6PA") should be(true)
  }

  "9AAA" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("6DFA") should be(true)
  }

  "99A" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("36A") should be(true)
  }

  "99AA" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("65NG") should be(true)
  }

  "99AAA" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("65ADF") should be(true)
  }

  "999A" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("697A") should be(true)
  }

  "999AA" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("696AT") should be(true)
  }

  "999AAA" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("692ATS") should be(true)
  }

  "9999A" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("8976D") should be(true)
  }

  "9999AA" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("8976DF") should be(true)
  }

  "A9AAA" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("G7GTY") should be(true)
  }

  "A99AAA" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("K77FGH") should be(true)
  }

  "A999AAA" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("Y897DFS") should be(true)
  }

  "AA99AAA" should "be a valid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("KL67HJK") should be(true)
  }

  // invalid cases
  "AA9AAA9" should "be an invalid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("KL7KLK8") should be(false)
  }

  "99999" should "be an invalid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("89898") should be(false)
  }

  "A99999" should "be an invalid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("K89898") should be(false)
  }

  "AAAA9" should "be an invalid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("QWRT8") should be(false)
  }

  "AAAA" should "be an invalid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("QWRT") should be(false)
  }

  "A9A" should "be an invalid registration number" in {
    InputFormatUtils.hasValidRegistrationNumberFormat("Q9D") should be(false)
  }

}
