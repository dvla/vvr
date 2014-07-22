package uk.gov.dvla.utils

import org.joda.time.DateMidnight


object InputFormatUtils {

  private val registrationNumberRegex =
    """[A-Z][0-9]{1,3}[A-Z]{3}|[A-Z][0-9]{1,4}|[A-Z]{2}[0-9]{1,4}|[A-Z]{2}[0-9]{2}[A-Z]{3}|[A-Z]{3}[0-9]{1,3}[A-Z]|[A-Z]{3}[0-9]{1,4}|[0-9]{1,3}[A-Z]{1,3}|[0-9]{4}[A-Z]{1,2}"""

  def convertRegistrationNumberToUnifiedFormat(registrationNumber: String) = {
    removeWhiteSpaces(registrationNumber).toUpperCase
  }

  def equalRegistrationNumbers(first: String, second: String) = {
    removeWhiteSpaces(first).equalsIgnoreCase(removeWhiteSpaces(second))
  }

  def equalFleetNumbers(first: String, second: String) = {
    removeWhiteSpaces(first) == removeWhiteSpaces(second)
  }

  def equalMakes(first: String, second: String) = {
    removeWhiteSpacesAroundCharacters(first.trim).equalsIgnoreCase(removeWhiteSpacesAroundCharacters(second.trim))
  }

  /*
   * Allow five digits followed by a dash, or six digits
   */
  def hasValidFleetNumberFormat(fleetNumber: String): Boolean = {
    fleetNumber.trim.matches("\\d{5}-") || fleetNumber.trim.matches("\\d{6}")
  }

  /*
   * Allow 2 to 7 alphanumeric characters, excluding spaces
   */
  def hasValidRegistrationNumberFormat(registrationNumber: String): Boolean = {
    val convertedRegistrationNumber = convertRegistrationNumberToUnifiedFormat(registrationNumber)
    convertedRegistrationNumber.matches("\\w{2,7}") && convertedRegistrationNumber.matches(registrationNumberRegex)
  }

  /*
   * Allows alphanumeric characters, whitespaces and + - ( ) characters. Limits between 1 and 30 characters
   */
  def hasValidMakeFormat(make: String) = {
    make.matches("[\\sa-zA-Z 0-9()+-/]{1,30}")
  }

  private def removeWhiteSpaces(string: String) = {
    string.replaceAll("\\s", "")
  }

  private def removeWhiteSpacesAroundCharacters(string: String, characters: Array[String] = Array("+", "-", "/", "(", ")")): String = {
    var result = string.trim
    characters.map(c => result = result.replaceAll("[\\s]*["+c+"][\\s]*", c))
    result
  }

  def isValidMonth(month: Int) = {
    Range(1,12).inclusive.contains(month)
  }

  def isValidYear(year: Int) = {
    Range(1900, 2099).inclusive.contains(year)
  }

  def dateFromSameAsOrBeforeDateTo(monthFrom: Int, yearFrom: Int, monthTo: Int, yearTo: Int): Boolean = {
    !new DateMidnight(yearTo, monthTo, 1).isBefore(new DateMidnight(yearFrom, monthFrom, 1))
  }

}
