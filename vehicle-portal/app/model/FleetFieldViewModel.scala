package model

import play.api.data.Field
import common.PortalDateFormatter

/**
 * view model wrapper for a Play Field
 */
class FleetFieldViewModel(field: Field, val labelName: String, val checkboxes: List[String]) {

  val name = field.name

  val value = field.value

  val errors = field.errors

  val isDateRangeFieldValid = FleetFieldViewModel.isDateRangeFieldValid(field)

  def isChecked(fieldName: String) = {
    if (!fieldName.isEmpty && field(fieldName).value.isDefined)
      "checked"
    else
      ""
  }

  def dateFieldValue(fieldName: String, dateType: String): Option[String] = {
    if (field(fieldName).hasErrors) {
      field(fieldName).value // don't reformat invalid values
    } else {
      field(fieldName).value.map( value =>
        if (dateType == DateInputType.MONTH && !value.isEmpty)
          PortalDateFormatter.formatMonth(value)
        else
          value
      )
    }
  }

}

object DateInputType {
  val MONTH = "month"
  val YEAR = "year"
}

object FleetFieldViewModel {

  import utils.DateFieldSuffix._

  def isDateRangeFieldValid(field: Field) = {
    // composite date field is valid if top level field is error free and the individual sub-fields are error free
    !field.hasErrors &&
      field(MONTH_FROM).errors ++ field(YEAR_FROM).errors ++ field(MONTH_TO).errors ++ field(YEAR_TO).errors == Nil
  }

}