package uk.gov.dvla.domain.criteria

import org.joda.time.DateMidnight

trait DateRangeCriteria {
  val monthFrom: Option[Int]
  val yearFrom: Option[Int]
  val monthTo: Option[Int]
  val yearTo: Option[Int]

  val dateFromIsDefined = monthFrom.isDefined && yearFrom.isDefined
  val dateFromIsEmpty = monthFrom.isEmpty && yearFrom.isEmpty
  val dateToIsDefined = monthTo.isDefined && yearTo.isDefined
  val dateToIsEmpty = monthTo.isEmpty && yearTo.isEmpty

  val dateFrom =
    if (dateFromIsDefined)
      Some(new DateMidnight(yearFrom.get, monthFrom.get, 1).toDate)
    else
      None

  val dateTo =
    if (dateFrom.isDefined && dateToIsDefined)
      Some(new DateMidnight(yearTo.get, monthTo.get, 1).plusMonths(1).toDate)
    else if (dateFrom.isDefined)
      Some(new DateMidnight(yearFrom.get, monthFrom.get, 1).plusMonths(1).toDate)
    else
      None
}

trait MOTExpiryCriteria extends DateRangeCriteria {
  val expired: Option[Boolean]
  val noDateHeld: Option[Boolean]

  def flattenToServiceQueryParams: Map[String, String] = {
    Map(
    "motMonthFrom" -> monthFrom,
    "motYearFrom" -> yearFrom,
    "motMonthTo" -> monthTo,
    "motYearTo" -> yearTo,
    "motExpired" -> expired,
    "motNoDateHeld" -> noDateHeld
    ).flatMap {case(k,v) => v.map(k -> _.toString)}
  }
}

trait FirstRegistrationCriteria extends DateRangeCriteria {
  def flattenToServiceQueryParams: Map[String, String] = {
    Map(
      "firstRegMonthFrom" -> monthFrom,
      "firstRegYearFrom" -> yearFrom,
      "firstRegMonthTo" -> monthTo,
      "firstRegYearTo" -> yearTo
    ).flatMap {case(k,v) => v.map(k -> _.toString)}
  }
}

trait FilterCriteria {
  val filter: Option[String] // legacy alert filter
  val registrationNumber: Option[String]
  val vehicleMake: Option[String]
  val mot: MOTExpiryCriteria
  val firstReg: FirstRegistrationCriteria

  def flattenToServiceQueryParams: Map[String, String] = {
    Map(
      "filter" -> filter,
      "registrationNumber" -> registrationNumber,
      "make" -> vehicleMake
    ).flatMap {case (k,v) => v.map(k -> _)} ++ mot.flattenToServiceQueryParams ++ firstReg.flattenToServiceQueryParams
  }
}


