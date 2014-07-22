package uk.gov.dvla.services.enquiry.vehicle.sql

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.dvla.services.common.{FirstRegistrationCriteriaParams, FilterCriteriaParams, MOTExpiryCriteriaParams}
import org.joda.time.DateMidnight


class SqlBuilderSpec extends FlatSpec with Matchers {

  // BUILD WHERE STATEMENT
  "MOTExpiryCriteriaSqlBuilder.buildWhere on the empty mot mot filter" should "return the empty string" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams()

    MOTExpiryCriteriaSqlBuilder(motExpiryCriteriaParams).buildWhere should be ("")
  }

  "MOTExpiryCriteriaSqlBuilder.buildWhere on mot filter with expired option" should "return a valid sql condition" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(expired = Some(true))

    MOTExpiryCriteriaSqlBuilder(motExpiryCriteriaParams).buildWhere should be ("(mot_expiry < now())")
  }

  "MOTExpiryCriteriaSqlBuilder.buildWhere on mot filter with expired and no date held options" should "return a valid sql condition" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(expired = Some(true), noDateHeld = Some(true))

    MOTExpiryCriteriaSqlBuilder(motExpiryCriteriaParams).buildWhere should be ("(mot_expiry is NULL OR mot_expiry < now())")
  }

  "MOTExpiryCriteriaSqlBuilder.buildWhere on mot filter with valid date range" should "return a valid sql condition" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(Some(1), Some(2012), Some(2), Some(2015))

    MOTExpiryCriteriaSqlBuilder(motExpiryCriteriaParams).buildWhere should be ("((mot_expiry >= :motDateFrom AND mot_expiry < :motDateTo))")
  }

  "MOTExpiryCriteriaSqlBuilder.buildWhere on mot filter with defined date from" should "return a valid sql condition" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(Some(1), Some(2012))

    MOTExpiryCriteriaSqlBuilder(motExpiryCriteriaParams).buildWhere should be ("((mot_expiry >= :motDateFrom AND mot_expiry < :motDateTo))")
  }

  "MOTExpiryCriteriaSqlBuilder.buildWhere on mot filter with incorrectly defined date range" should "return the empty string" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(Some(1))

    MOTExpiryCriteriaSqlBuilder(motExpiryCriteriaParams).buildWhere should be ("")
  }

  "MOTExpiryCriteriaSqlBuilder.buildWhere on mot filter with valid date range and expired and no date held options" should "return a valid sql condition" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(Some(1), Some(2012), Some(2), Some(2015), Some(true), Some(true))

    MOTExpiryCriteriaSqlBuilder(motExpiryCriteriaParams).buildWhere should be ("((mot_expiry >= :motDateFrom AND mot_expiry < :motDateTo) OR mot_expiry is NULL OR mot_expiry < now())")
  }

  "FirstRegistrationCriteriaSqlBuilder.buildWhere on empty filter" should "return the empty string" in {
    val criteriaParams = FirstRegistrationCriteriaParams()
    FirstRegistrationCriteriaSqlBuilder(criteriaParams).buildWhere should be ("")
  }
  "FirstRegistrationCriteriaSqlBuilder.buildWhere on filter with defined date from" should "return a valid sql condition" in {
    val criteriaParams = FirstRegistrationCriteriaParams(Some(1), Some(2012))
    FirstRegistrationCriteriaSqlBuilder(criteriaParams).buildWhere should be ("(first_registration >= :firstRegDateFrom AND first_registration < :firstRegDateTo)")
  }
  "FirstRegistrationCriteriaSqlBuilder.buildWhere on filter with valid date range" should "return a valid sql condition" in {
    var criteriaParams = FirstRegistrationCriteriaParams(Some(1), Some(2012), Some(2), Some(2015))
    FirstRegistrationCriteriaSqlBuilder(criteriaParams).buildWhere should be ("(first_registration >= :firstRegDateFrom AND first_registration < :firstRegDateTo)")
  }
  "FirstRegistrationCriteriaSqlBuilder.buildWhere on filter with incorrectly defined date range" should "return the empty string" in {
    val criteriaParams = FirstRegistrationCriteriaParams(Some(1))
    FirstRegistrationCriteriaSqlBuilder(criteriaParams).buildWhere should be ("")
  }

  "FilterCriteriaSqlBuilder.buildWhere on a filter " should "return a valid where sql statement" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(Some(1), Some(2012), Some(2), Some(2015), Some(true), Some(true))
    val filterCriteriaParams = FilterCriteriaParams("123456", Some("all") , Some("AAB911A"), Some("FORD"), motExpiryCriteriaParams, new FirstRegistrationCriteriaParams)

    FilterCriteriaSqlBuilder(filterCriteriaParams).buildWhere should be ("WHERE ((mot_expiry >= :motDateFrom AND mot_expiry < :motDateTo) OR mot_expiry is NULL OR mot_expiry < now()) AND make = :make AND registration_number = :registrationNumber AND fleet_number = :fleetNumber")
  }

  "FilterCriteriaSqlBuilder.buildWhere on an empty filter " should "return a valid where sql statement" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams()
    val filterCriteriaParams = FilterCriteriaParams("123456", None , None, None, motExpiryCriteriaParams, new FirstRegistrationCriteriaParams)

    FilterCriteriaSqlBuilder(filterCriteriaParams).buildWhere should be ("WHERE fleet_number = :fleetNumber")
  }

  // FLATTEN TO BINDING MAP
  "MOTExpiryCriteriaSqlBuilder.flattenToSqlBindingMap on mot filter" should "return a valid map with binding parameters" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(Some(1), Some(2012), Some(2), Some(2015), Some(true), Some(true))

    MOTExpiryCriteriaSqlBuilder(motExpiryCriteriaParams).flattenToSqlBindingMap should be (
      Map("motDateFrom" -> new DateMidnight(2012, 1, 1).toDate,
        "motDateTo" -> new DateMidnight(2015, 3, 1).toDate,
        "motExpired" -> Boolean.box(true),
        "motNoDateHeld" -> Boolean.box(true)
      )
    )
  }

  "FirstRegistrationCriteriaSqlBuilder.flattenToSqlBindingMap on mot filter" should "return a valid map with binding parameters" in {
    val criteriaParams = FirstRegistrationCriteriaParams(Some(1), Some(2012), Some(2), Some(2015))

    FirstRegistrationCriteriaSqlBuilder(criteriaParams).flattenToSqlBindingMap should be (
      Map("firstRegDateFrom" -> new DateMidnight(2012, 1, 1).toDate,
          "firstRegDateTo" -> new DateMidnight(2015, 3, 1).toDate
      )
    )
  }

  "FilterCriteriaSqlBuilder.flattenToSqlBindingMap on fleet/mot filters" should "return a valid map with binding parameters" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(Some(1), Some(2012), Some(2), Some(2015), Some(true), Some(true))
    val filterCriteriaParams = FilterCriteriaParams("123456", None , None, None, motExpiryCriteriaParams, new FirstRegistrationCriteriaParams)

    FilterCriteriaSqlBuilder(filterCriteriaParams).flattenToSqlBindingMap should be (
      Map(
        "fleetNumber" -> "123456",
        "motDateFrom" -> new DateMidnight(2012, 1, 1).toDate,
        "motDateTo" -> new DateMidnight(2015, 3, 1).toDate,
        "motExpired" -> Boolean.box(true),
        "motNoDateHeld" -> Boolean.box(true)
      )
    )
  }

  "FilterCriteriaSqlBuilder.flattenToSqlBindingMap on fleet/mot/firstReg filters" should "return a valid map with binding parameters" in {
    val firstRegCriteriaParams = FirstRegistrationCriteriaParams(Some(2), Some(2013), Some(3), Some(2016))
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(Some(1), Some(2012), Some(2), Some(2015), Some(true), Some(true))
    val filterCriteriaParams = FilterCriteriaParams("123456", None , None, None, motExpiryCriteriaParams, firstRegCriteriaParams)

    FilterCriteriaSqlBuilder(filterCriteriaParams).flattenToSqlBindingMap should be (
      Map(
        "fleetNumber" -> "123456",
        "motDateFrom" -> new DateMidnight(2012, 1, 1).toDate,
        "motDateTo" -> new DateMidnight(2015, 3, 1).toDate,
        "motExpired" -> Boolean.box(true),
        "motNoDateHeld" -> Boolean.box(true),
        "firstRegDateFrom" -> new DateMidnight(2013, 2, 1).toDate,
        "firstRegDateTo" -> new DateMidnight(2016, 4, 1).toDate
      )
    )
  }

  // BUILD ORDER BY STATEMENT
  "MOTExpiryCriteriaSqlBuilder.buildOrderBy when no date held is selected" should "return a correct ordering rule" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(Some(1), Some(2012), Some(2), Some(2015), Some(true), Some(true))

    MOTExpiryCriteriaSqlBuilder(motExpiryCriteriaParams).buildOrderBy should be (" order by mot_expiry asc nulls first, first_registration asc ")
  }

  "MOTExpiryCriteriaSqlBuilder.buildOrderBy when mot filter is empty" should "return the empty string" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams()

    MOTExpiryCriteriaSqlBuilder(motExpiryCriteriaParams).buildOrderBy should be ("")
  }

  "MOTExpiryCriteriaSqlBuilder.buildOrderBy when no date held is not selected" should "return a correct ordering rule" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(Some(1), Some(2012), Some(2), Some(2015), Some(true), None)

    MOTExpiryCriteriaSqlBuilder(motExpiryCriteriaParams).buildOrderBy should be (" order by mot_expiry asc ")
  }

  "FirstRegistrationCriteriaSqlBuilder.buildOrderBy when first reg filter is empty" should "return the empty string" in {
    val criteriaParams = FirstRegistrationCriteriaParams()
    FirstRegistrationCriteriaSqlBuilder(criteriaParams).buildOrderBy should be ("")
  }
  "FirstRegistrationCriteriaSqlBuilder.buildOrderBy when first reg filter is not empty" should "return a correct ordering rule" in {
    val criteriaParams = FirstRegistrationCriteriaParams(Some(1), Some(2012), Some(2), Some(2015))
    FirstRegistrationCriteriaSqlBuilder(criteriaParams).buildOrderBy should be (" order by first_registration asc ")
  }

  "FilterCriteriaSqlBuilder.buildOrderBy when more than one date filter is specified" should "return the default ordering rule" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(Some(1), Some(2012), Some(2), Some(2015), Some(true), None)
    val filterCriteriaParams = FilterCriteriaParams("123456", Some("old") , None, None, motExpiryCriteriaParams, new FirstRegistrationCriteriaParams)

    FilterCriteriaSqlBuilder(filterCriteriaParams).buildOrderBy should be (" order by first_registration desc , registration_number ")
  }

  "FilterCriteriaSqlBuilder.buildOrderBy when only one date filter is specified" should "return the rule from this specified date filter" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(Some(1), Some(2012), Some(2), Some(2015), Some(true), None)
    val filterCriteriaParams = FilterCriteriaParams("123456", None , None, None, motExpiryCriteriaParams, new FirstRegistrationCriteriaParams)

    FilterCriteriaSqlBuilder(filterCriteriaParams).buildOrderBy should be (" order by mot_expiry asc , registration_number ")
  }

  "FilterCriteriaSqlBuilder.buildOrderBy when mot filter is empty and 'old' filter is selected" should "order by first_registration asc" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams()
    val filterCriteriaParams = FilterCriteriaParams("123456", Some("old") , None, None, motExpiryCriteriaParams, new FirstRegistrationCriteriaParams)

    FilterCriteriaSqlBuilder(filterCriteriaParams).buildOrderBy should be (" order by first_registration asc , registration_number ")
  }

}
