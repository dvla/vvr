package uk.gov.dvla.time

import java.util.{Calendar, Date}
import org.joda.time.{DateMidnight, DateTime}


trait TimeUtils {
  def currentTime: Date

  def currentDateTime: DateTime

  def currentDateMidnight: DateMidnight
}

object TimeUtils extends TimeUtils {

  def currentTime: Date = {
    currentDateTime.toDate
  }

  def currentDateTime: DateTime = {
    DateTime.now
  }

  def currentDateMidnight: DateMidnight = {
    currentDateTime.toDateMidnight
  }

  def getStartOfToday: Calendar = {
    var cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal
  }
}
