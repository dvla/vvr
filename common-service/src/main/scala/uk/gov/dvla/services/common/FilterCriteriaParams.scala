package uk.gov.dvla.services.common

import uk.gov.dvla.domain.criteria.{FirstRegistrationCriteria, MOTExpiryCriteria, FilterCriteria}
import org.joda.time.DateMidnight
import uk.gov.dvla.time.TimeUtils
import uk.gov.dvla.domain.FleetFilter

case class FilterCriteriaParams(fleetNumber: String, filter: Option[String], registrationNumber: Option[String], vehicleMake: Option[String], mot: MOTExpiryCriteriaParams, firstReg: FirstRegistrationCriteriaParams)
  extends FilterCriteria {

  val liability =
    if(filter == Some(FleetFilter.DUE))
      Some(TimeUtils.currentDateMidnight.plusDays(60).toDate)
    else
      None

  val firstRegistration =
    if(filter == Some(FleetFilter.OLD))
      Some(TimeUtils.currentDateMidnight.minusMonths(30).toDate)
    else
      None

}

case class MOTExpiryCriteriaParams(monthFrom: Option[Int] = None, yearFrom: Option[Int] = None, monthTo: Option[Int] = None, yearTo: Option[Int] = None, expired: Option[Boolean] = None, noDateHeld: Option[Boolean] = None)
  extends MOTExpiryCriteria {

  def isEmpty = this == MOTExpiryCriteriaParams()
}

case class FirstRegistrationCriteriaParams(monthFrom: Option[Int] = None, yearFrom: Option[Int] = None, monthTo: Option[Int] = None, yearTo: Option[Int] = None)
  extends FirstRegistrationCriteria {

  def isEmpty = this == FirstRegistrationCriteriaParams()
}

object FilterCriteriaParams {

  def apply(fleetNumber: String, requestParams: Map[String, String]): FilterCriteriaParams = {
    val filter = requestParams.get("filter")
    val reg = requestParams.get("registrationNumber").map(_.toUpperCase)
    val make = requestParams.get("make").map(_.toUpperCase)
    val mot = MOTExpiryCriteriaParams(requestParams)
    val firstReg = FirstRegistrationCriteriaParams(requestParams)

    FilterCriteriaParams(fleetNumber, filter, reg, make, mot, firstReg)
  }
}

object MOTExpiryCriteriaParams {

  def apply(requestParams: Map[String, String]): MOTExpiryCriteriaParams = {
    val motMonthFrom = requestParams.get("motMonthFrom").map(_.toInt)
    val motYearFrom = requestParams.get("motYearFrom").map(_.toInt)
    val motMonthTo = requestParams.get("motMonthTo").map(_.toInt)
    val motYearTo = requestParams.get("motYearTo").map(_.toInt)
    val motExpired = requestParams.get("motExpired").map(_.toBoolean)
    val motNoDateHeld = requestParams.get("motNoDateHeld").map(_.toBoolean)

    MOTExpiryCriteriaParams(motMonthFrom, motYearFrom, motMonthTo, motYearTo, motExpired, motNoDateHeld)
  }
}

object FirstRegistrationCriteriaParams {

  def apply(requestParams: Map[String, String]): FirstRegistrationCriteriaParams = {
    val firstRegMonthFrom = requestParams.get("firstRegMonthFrom").map(_.toInt)
    val firstRegYearFrom = requestParams.get("firstRegYearFrom").map(_.toInt)
    val firstRegMonthTo = requestParams.get("firstRegMonthTo").map(_.toInt)
    val firstRegYearTo = requestParams.get("firstRegYearTo").map(_.toInt)

    FirstRegistrationCriteriaParams(firstRegMonthFrom, firstRegYearFrom, firstRegMonthTo, firstRegYearTo)
  }
}