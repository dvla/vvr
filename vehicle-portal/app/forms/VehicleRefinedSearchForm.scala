package forms

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.dvla.utils.InputFormatUtils._
import model.{FirstRegistrationFilter, MOTExpiryFilter, VehicleRefinedSearchData}
import VehicleRefinedSearchForm._
import utils.DateFieldSuffix

trait VehicleRefinedSearchForm {

  val vehicleRefinedSearchForm = Form(
    mapping(
      FILTER_PARAM -> optional(text),
      REGISTRATION_PARAM -> optional(text.verifying("vvrform.enter.a.valid.vehicle.registration.number", hasValidRegistrationNumberFormat _)),
      MAKE_PARAM -> optional(text.verifying("vvrform.enter.a.valid.vehicle.make", hasValidMakeFormat _)),
      MOT_PARAM -> mapping(
        DateFieldSuffix.MONTH_FROM -> optional(number.verifying("vvrform.enter.a.valid.date", isValidMonth _)),
        DateFieldSuffix.YEAR_FROM -> optional(number.verifying("vvrform.enter.a.valid.date", isValidYear _)),
        DateFieldSuffix.MONTH_TO -> optional(number.verifying("vvrform.enter.a.valid.date", isValidMonth _)),
        DateFieldSuffix.YEAR_TO -> optional(number.verifying("vvrform.enter.a.valid.date", isValidYear _)),
        EXPIRED_PARAM -> optional(boolean),
        NO_DATE_PARAM -> optional(boolean)
      )(MOTExpiryFilter.apply)(MOTExpiryFilter.unapply).verifying("vvrform.enter.a.valid.date.range", VehicleRefinedSearchForm.validMOTExpiryFilterDateRange _),
      FIRST_REGISTRATION_PARAM -> mapping(
        DateFieldSuffix.MONTH_FROM -> optional(number.verifying("vvrform.enter.a.valid.date", isValidMonth _)),
        DateFieldSuffix.YEAR_FROM -> optional(number.verifying("vvrform.enter.a.valid.date", isValidYear _)),
        DateFieldSuffix.MONTH_TO -> optional(number.verifying("vvrform.enter.a.valid.date", isValidMonth _)),
        DateFieldSuffix.YEAR_TO -> optional(number.verifying("vvrform.enter.a.valid.date", isValidYear _))
      )(FirstRegistrationFilter.apply)(FirstRegistrationFilter.unapply).verifying("vvrform.enter.a.valid.date.range", VehicleRefinedSearchForm.validFirstRegFilterDateRange _)
    )(VehicleRefinedSearchData.cleanApply)(VehicleRefinedSearchData.unapply)
  )

}

object VehicleRefinedSearchForm {

  val FILTER_PARAM = "filter"
  val REGISTRATION_PARAM = "reg"
  val MAKE_PARAM = "mk"
  val LIABILITY_PARAM = "dol"
  val FIRST_REGISTRATION_PARAM = "firstReg"
  val MOT_PARAM = "mot"

  val EXPIRED_PARAM = "expired"
  val NO_DATE_PARAM = "noDateHeld"

  val MOT_MONTH_FROM_PARAM = MOT_PARAM + "." + DateFieldSuffix.MONTH_FROM
  val MOT_YEAR_FROM_PARAM = MOT_PARAM + "." + DateFieldSuffix.YEAR_FROM
  val MOT_MONTH_TO_PARAM = MOT_PARAM + "." + DateFieldSuffix.MONTH_TO
  val MOT_YEAR_TO_PARAM = MOT_PARAM + "." + DateFieldSuffix.YEAR_TO
  val MOT_EXPIRED_PARAM = MOT_PARAM + "." + EXPIRED_PARAM
  val MOT_NO_DATE_PARAM = MOT_PARAM + "." + NO_DATE_PARAM

  def validMOTExpiryFilterDateRange(mot: MOTExpiryFilter): Boolean = {
    if (mot.dateFromIsEmpty && mot.dateToIsEmpty)
      true
    else {
      if (mot.dateFromIsDefined && mot.dateToIsEmpty) {
        true
      } else if (mot.dateFromIsDefined && mot.dateToIsDefined) {
        dateFromSameAsOrBeforeDateTo(mot.monthFrom.get, mot.yearFrom.get, mot.monthTo.get, mot.yearTo.get)
      } else {
        false
      }
    }
  }

  val FIRST_REG_MONTH_FROM_PARAM = FIRST_REGISTRATION_PARAM + "." + DateFieldSuffix.MONTH_FROM
  val FIRST_REG_YEAR_FROM_PARAM = FIRST_REGISTRATION_PARAM + "." + DateFieldSuffix.YEAR_FROM
  val FIRST_REG_MONTH_TO_PARAM = FIRST_REGISTRATION_PARAM + "." + DateFieldSuffix.MONTH_TO
  val FIRST_REG_YEAR_TO_PARAM = FIRST_REGISTRATION_PARAM + "." + DateFieldSuffix.YEAR_TO

  def validFirstRegFilterDateRange(firstReg: FirstRegistrationFilter): Boolean = {
    if (firstReg.dateFromIsEmpty && firstReg.dateToIsEmpty)
      true
    else {
      if (firstReg.dateFromIsDefined && firstReg.dateToIsEmpty) {
        true
      } else if (firstReg.dateFromIsDefined && firstReg.dateToIsDefined) {
        dateFromSameAsOrBeforeDateTo(firstReg.monthFrom.get, firstReg.yearFrom.get, firstReg.monthTo.get, firstReg.yearTo.get)
      } else {
        false
      }
    }
  }
}
