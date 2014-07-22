package uk.gov.dvla.utils

import org.scalatest._
import uk.gov.dvla.utils.InputFormatUtils._


class InputFormatUtilsSpec extends FlatSpec with Matchers {

  "Upper case vehicle make" should "match with lower case vehicle make" in {
    equalMakes("Mazda", "MAZDA") should be (true)
  }

  "Vehicle makes with different trailing white spaces" should "match" in {
    equalMakes("  Mazda", " Mazda   ") should be (true)
  }

  "Vehicle make with white space inside" should "not match with one without white space inside" in {
    equalMakes("Ma zda", "Mazda") should be (false)
  }

  "Registration number matching" should "not be case sensitive" in {
    equalRegistrationNumbers("FH67FL", "fh67fl") should be (true)
  }

  "Registration number matching" should "ignore white spaces" in {
    equalRegistrationNumbers(" FL 78 ", "FL78") should be (true)
  }

  "Fleet number matching" should "ignore white spaces" in {
    equalFleetNumbers(" 12 34 5-", "12345-") should be (true)
  }

  "Convert registration number" should "change it to uppercase" in {
    convertRegistrationNumberToUnifiedFormat("fl56w") should be ("FL56W")
  }

  "Convert registration number" should "remove white spaces" in {
    convertRegistrationNumberToUnifiedFormat(" FL 56 W ") should be ("FL56W")
  }

  "hasValidFleetNumberFormat" should "allow five digits and dash" in {
    hasValidFleetNumberFormat("12345-") should be (true)
  }

  "hasValidFleetNumberFormat" should "allow six digits" in {
    hasValidFleetNumberFormat("123456") should be (true)
  }

  "hasValidFleetNumberFormat" should "not allow spaces in the middle" in {
    hasValidFleetNumberFormat("123 456") should be (false)
  }

  "hasValidFleetNumberFormat" should "allow spaces before and after" in {
    hasValidFleetNumberFormat(" 123456 ") should be (true)
  }

  "hasValidFleetNumberFormat" should "not allow dash before the end" in {
    hasValidFleetNumberFormat("123-56") should be (false)
  }

  "hasValidRegistrationNumberFormat" should "disallow single character" in {
    hasValidRegistrationNumberFormat("A") should be (false)
  }

  "hasValidRegistrationNumberFormat" should "allow spaces" in {
    hasValidRegistrationNumberFormat("AA99 XYZ") should be (true)
  }

  "hasValidRegistrationNumberFormat" should "disallow eight non-space characters" in {
    hasValidRegistrationNumberFormat("AA99 XYZZ") should be (false)
  }

  "hasValidMakeFormat" should "not allow *" in {
    hasValidMakeFormat("Mazda*") should be (false)
  }

  "hasValidMakeFormat" should "not allow 31 characters" in {
    hasValidMakeFormat("qwertyuiopasdfghjklzxcvbnmqwert") should be (false)
  }

  "hasValidMakeFormat" should "allow alphanumerics, + - ( ) / and whitespaces" in {
    hasValidMakeFormat("Mazda / (+-) ") should be (true)
  }

  "Vehicle makes with different white spaces around + - ( ) /" should "match" in {
    equalMakes("NIESMANN + BISCHOFF", "NIESMANN+BISCHOFF") should be (true)
    equalMakes("NIESMANN +BISCHOFF", "NIESMANN+ BISCHOFF") should be (true)
    equalMakes("NIESMANN - BISCHOFF", "NIESMANN-BISCHOFF") should be (true)
    equalMakes("NIESMANN  /  BISCHOFF", "NIESMANN/ BISCHOFF") should be (true)    //first string uses tab space around /
  }

  "Months between 1 and 12" should "be a valid month" in {
    for (month <- 1 to 12) {
      isValidMonth(month) should be (true)
    }
  }

  "Month 0" should "not be a valid month" in {
    isValidMonth(0) should be (false)
  }

  "Year 23415" should "be an invalid year" in {
    isValidYear(23415) should be (false)
  }

  "Year -1" should "be an invalid year" in {
    isValidYear(-1) should be (false)
  }

}
