package uk.gov.dvla.services.common

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.TimeZone

object ServiceDateFormat {
  final val DEFAULT_DATE_FORMAT: String = "yyyy-MM-dd"
  final val TIME_ZONE: TimeZone = TimeZone.getTimeZone("GMT")
  final val DEFAULT: DateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT)
}