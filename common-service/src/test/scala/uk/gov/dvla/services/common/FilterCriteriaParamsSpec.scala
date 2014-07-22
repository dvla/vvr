package uk.gov.dvla.services.common

import org.scalatest.{Matchers, FlatSpec}
import org.joda.time.DateMidnight


class FilterCriteriaParamsSpec extends FlatSpec with Matchers {

  "MOTExpiryCriteriaParams.dateFrom" should "be the first day of a specified month and year" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(Some(1), Some(2012), Some(2), Some(2015), Some(true), Some(true))

    motExpiryCriteriaParams.dateFrom should be (Some(new DateMidnight(2012, 1, 1).toDate))
  }

  "MOTExpiryCriteriaParams.dateFrom" should "be None if montFrom and yearFrom not specified" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(None, None, None, None, Some(true), Some(true))

    motExpiryCriteriaParams.dateFrom should be (None)
  }

  "MOTExpiryCriteriaParams.dateTo" should "be the first day of a next month" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(Some(1), Some(2012), Some(2), Some(2015), Some(true), Some(true))

    motExpiryCriteriaParams.dateTo should be (Some(new DateMidnight(2015, 3, 1).toDate))
  }

  "MOTExpiryCriteriaParams.dateTo if no monthTo and yearTo specified" should "be one month later than dateFrom" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(Some(1), Some(2012))

    motExpiryCriteriaParams.dateTo should be (Some(new DateMidnight(2012, 2, 1).toDate))
  }

  "MOTExpiryCriteriaParams.dateTo" should "be None if date range is not specified" in {
    val motExpiryCriteriaParams = MOTExpiryCriteriaParams(None, None, None, None, Some(true), Some(true))

    motExpiryCriteriaParams.dateTo should be (None)
  }

}
