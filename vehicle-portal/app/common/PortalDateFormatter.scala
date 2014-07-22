package common

import java.text.SimpleDateFormat
import java.util.Date
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.apache.commons.lang.StringUtils

object PortalDateFormatter {

  // default date format i.e. 9 January 2000, not 09 January 2000
  val DEFAULT_DATE_FORMAT = "d MMMM yyyy"
  val SHORT_DATE_FORMAT = "d MMM yyyy"

  // convenience method to format a date for displaying in the portal (view)
  def format(date: Date): String = {
    if (date == null) {
      ""
    }
    else {
      new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(date)
    }
  }

  def shortDateFormat(date: Date) = {
    if (date == null) {
      ""
    }
    else {
      new SimpleDateFormat(SHORT_DATE_FORMAT).format(date)
    }
  }

  def ukFormat(date: Date) = {
    if (date == null) {
      ""
    }
    else {
      new SimpleDateFormat("dd/MM/YYYY").format(date)
    }
  }

  def yyyyMMdd(date: Date) = {
    format(date, "yyyyMMdd")
  }

  def yyyyMMddWithHyphens(date: Date) = {
    format(date, "yyyy-MM-dd")
  }

  def HHmmss(date: Date) = {
    format(date, "HHmmss")
  }

  def format(date: Date, format: String) = {
    val dt = new DateTime(date.getTime)
    DateTimeFormat.forPattern(format).print(dt)
  }

  def formatMonth(month: String) = {
    StringUtils.leftPad(month, 2, '0')
  }

}
