package model

import forms.VehicleRefinedSearchForm
import uk.gov.dvla.domain.criteria.{MOTExpiryCriteria, FilterCriteria, FirstRegistrationCriteria}
import uk.gov.dvla.domain.FleetFilter

trait QueryParameterizable {
  def toQueryMap: Map[String, Seq[String]]
}

case class VehicleRefinedSearchData(filter: Option[String] = None,
                                    registrationNumber: Option[String] = None,
                                    vehicleMake: Option[String] = None,
                                    mot: MOTExpiryFilter = MOTExpiryFilter(),
                                    firstReg: FirstRegistrationFilter = FirstRegistrationFilter())
  extends FilterCriteria with QueryParameterizable {

  override def toQueryMap: Map[String, Seq[String]] = {
    Map(
      VehicleRefinedSearchForm.FILTER_PARAM -> filter.toSeq,
      VehicleRefinedSearchForm.REGISTRATION_PARAM -> registrationNumber.toSeq,
      VehicleRefinedSearchForm.MAKE_PARAM -> vehicleMake.toSeq
    ) ++ mot.toQueryMap ++ firstReg.toQueryMap
  }

  lazy val isRefined = this != VehicleRefinedSearchData(filter)
}

object VehicleRefinedSearchData {
  val emptyFilter = VehicleRefinedSearchData()

  /**
   * applies non-fatal validation to filter input
   */
  def cleanApply(filter: Option[String], registrationNumber: Option[String], vehicleMake: Option[String], mot: MOTExpiryFilter, firstReg: FirstRegistrationFilter):VehicleRefinedSearchData = {
    VehicleRefinedSearchData(validateFilter(filter), registrationNumber, vehicleMake, mot, firstReg)
  }

  private def validateFilter(filter: Option[String]) = {
    filter match {
      case Some(value) => filter.map(FleetFilter.validateFilter)
      case None => Some(FleetFilter.ALL)
    }
  }
}

case class MOTExpiryFilter(monthFrom: Option[Int] = None, yearFrom: Option[Int] = None, monthTo: Option[Int] = None, yearTo: Option[Int] = None, expired: Option[Boolean] = None, noDateHeld: Option[Boolean] = None)
  extends MOTExpiryCriteria with QueryParameterizable {

  override def toQueryMap: Map[String, Seq[String]] = {
    Map(
      VehicleRefinedSearchForm.MOT_MONTH_FROM_PARAM -> monthFrom.map(_ toString).toSeq,
      VehicleRefinedSearchForm.MOT_YEAR_FROM_PARAM -> yearFrom.map(_ toString).toSeq,
      VehicleRefinedSearchForm.MOT_MONTH_TO_PARAM -> monthTo.map(_ toString).toSeq,
      VehicleRefinedSearchForm.MOT_YEAR_TO_PARAM -> yearTo.map(_ toString).toSeq,
      VehicleRefinedSearchForm.MOT_EXPIRED_PARAM -> expired.map(_ toString).toSeq,
      VehicleRefinedSearchForm.MOT_NO_DATE_PARAM -> noDateHeld.map(_ toString).toSeq
    )
  }

  lazy val isEmpty = this == MOTExpiryFilter()

}

case class FirstRegistrationFilter(monthFrom: Option[Int] = None, yearFrom: Option[Int] = None, monthTo: Option[Int] = None, yearTo: Option[Int] = None)
  extends FirstRegistrationCriteria with QueryParameterizable {

  override def toQueryMap: Map[String, Seq[String]] = {
    Map(
      VehicleRefinedSearchForm.FIRST_REG_MONTH_FROM_PARAM -> monthFrom.map(_ toString).toSeq,
      VehicleRefinedSearchForm.FIRST_REG_YEAR_FROM_PARAM -> yearFrom.map(_ toString).toSeq,
      VehicleRefinedSearchForm.FIRST_REG_MONTH_TO_PARAM -> monthTo.map(_ toString).toSeq,
      VehicleRefinedSearchForm.FIRST_REG_YEAR_TO_PARAM -> yearTo.map(_ toString).toSeq
    )
  }

  lazy val isEmpty = this == FirstRegistrationFilter()

}
