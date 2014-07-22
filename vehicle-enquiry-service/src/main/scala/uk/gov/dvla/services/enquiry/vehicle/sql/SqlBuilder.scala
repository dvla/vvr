package uk.gov.dvla.services.enquiry.vehicle.sql

import uk.gov.dvla.services.common.{FirstRegistrationCriteriaParams, MOTExpiryCriteriaParams, FilterCriteriaParams}


trait SqlBuilder {

  def buildWhere: String

  def buildOrderBy: String

}

case class FilterCriteriaSqlBuilder(criteria: FilterCriteriaParams)
  extends SqlBuilder {

  val motSqlBuilder = MOTExpiryCriteriaSqlBuilder(criteria.mot)
  val firstRegSqlBuilder = FirstRegistrationCriteriaSqlBuilder(criteria.firstReg)

  override def buildWhere: String = {
    var conditions = List[String]()

    conditions = "fleet_number = :fleetNumber" :: conditions

    criteria.filter match {
      case Some(uk.gov.dvla.domain.FleetFilter.DUE) => {
        conditions = "liability <= :liability" :: conditions
      }
      case Some(uk.gov.dvla.domain.FleetFilter.OLD) => {
        conditions = "first_registration <= :firstRegistration" :: conditions
      }
      case _ =>
    }

    if (criteria.registrationNumber.isDefined)
      conditions = "registration_number = :registrationNumber" :: conditions

    if (criteria.vehicleMake.isDefined)
      conditions = "make = :make" :: conditions

    if (!criteria.mot.isEmpty)
      conditions = motSqlBuilder.buildWhere :: conditions

    if (!criteria.firstReg.isEmpty)
      conditions = firstRegSqlBuilder.buildWhere :: conditions

    if (conditions.isEmpty)
      ""
    else
      "WHERE " + conditions.mkString(" AND ")
  }

  def flattenToSqlBindingMap: Map[String, Object] = {
    Map(
      "fleetNumber" -> Some(criteria.fleetNumber),
      "registrationNumber" -> criteria.registrationNumber,
      "make" -> criteria.vehicleMake,
      "liability" -> criteria.liability,
      "firstRegistration" -> criteria.firstRegistration
    ).flatMap { case(k,v) => if(v.isDefined) Some(k -> v.get) else None} ++ motSqlBuilder.flattenToSqlBindingMap ++ firstRegSqlBuilder.flattenToSqlBindingMap
  }

  override def buildOrderBy: String = {
    var orderingCriteria = List[String]()
    orderingCriteria = orderingCriteria :+ motSqlBuilder.buildOrderBy
    orderingCriteria = orderingCriteria :+ firstRegSqlBuilder.buildOrderBy

    if (criteria.liability.isDefined)
      orderingCriteria = orderingCriteria :+ " order by liability asc "

    if (criteria.firstRegistration.isDefined)
      orderingCriteria = orderingCriteria :+  " order by first_registration asc "

    val notEmptyOrderingCriteria = orderingCriteria.filterNot(_.isEmpty)
    if (notEmptyOrderingCriteria.size == 1)
      notEmptyOrderingCriteria.last + ", registration_number "
    else
      " order by first_registration desc , registration_number "
  }
}

case class MOTExpiryCriteriaSqlBuilder(criteria: MOTExpiryCriteriaParams)
  extends SqlBuilder {

  override def buildWhere: String = {
    var conditions = List[String]()
    if (criteria.expired.exists(identity)) {
      conditions = "mot_expiry < now()" :: conditions
    }
    if (criteria.noDateHeld.exists(identity)) {
      conditions = "mot_expiry is NULL" :: conditions
    }
    if (criteria.dateFrom.isDefined && criteria.dateTo.isDefined) {
      conditions = "(mot_expiry >= :motDateFrom AND mot_expiry < :motDateTo)" :: conditions
    }
    if (conditions.isEmpty)
      ""
    else
      conditions.mkString("(",  " OR ", ")")
  }

  def flattenToSqlBindingMap: Map[String, Object] = {
    Map(
      "motDateFrom" -> criteria.dateFrom,
      "motDateTo" -> criteria.dateTo,
      "motExpired" -> criteria.expired.map(Boolean.box(_)),
      "motNoDateHeld" -> criteria.noDateHeld.map(Boolean.box(_))
    ).flatMap {case (k,v) => v.map(k -> _)}
  }

  override def buildOrderBy: String = {
    if (criteria.isEmpty)
      return ""
    else
      if (criteria.noDateHeld.exists(identity))
        return " order by mot_expiry asc nulls first, first_registration asc "
      else
        return " order by mot_expiry asc "
  }

}

case class FirstRegistrationCriteriaSqlBuilder(criteria: FirstRegistrationCriteriaParams)
  extends SqlBuilder {

  override def buildWhere: String = {
    if (criteria.dateFrom.isDefined && criteria.dateTo.isDefined)
      "(first_registration >= :firstRegDateFrom AND first_registration < :firstRegDateTo)"
    else
      ""
  }

  def flattenToSqlBindingMap: Map[String, Object] = {
    Map(
      "firstRegDateFrom" -> criteria.dateFrom,
      "firstRegDateTo" -> criteria.dateTo
    ).flatMap {case (k,v) => v.map(k -> _)}
  }

  override def buildOrderBy: String = {
      if (criteria.isEmpty)
        ""
      else
        " order by first_registration asc "
  }

}




