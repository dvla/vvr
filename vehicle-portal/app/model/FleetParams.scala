package model

import play.api.mvc.{AnyContent, Request}
import forms.VehicleRefinedSearchForm
import org.slf4j.LoggerFactory
import play.api.data.Form

class FleetParams(val form: Form[VehicleRefinedSearchData]) {

  private val logger = LoggerFactory.getLogger(getClass)

  /**
   * data should represent the valid data from the form/query, ignoring error fields
   */
  val data = {
    if (!form.hasErrors)
      form.value.get
    else {
      // filter out fields that have errors and re-bind
      val errorFieldNames = form.errors.map{ case error => keyOrPrefix(error.key) }
      val validFields = form.data.collect{ case (k, v) if !isErrorField(k, errorFieldNames) => (k, Seq(v)) }
      FleetParams.vehicleRefinedSearchForm.bindFromRequest(validFields).get
    }
  }

  private def isErrorField(key:String, errorFieldNames:Seq[String]) = {
    errorFieldNames.contains(key) || errorFieldNames.exists(errKey => key.startsWith(errKey+"."))
  }

  private def keyOrPrefix(key:String) = {
    // if the field contains . assume it is a prefix for nested fields
    val idx = key.indexOf(".")
    if (idx > 0)
      key.substring(0, idx)
    else
      key
  }

}

object FleetParams extends VehicleRefinedSearchForm {

  private val logger = LoggerFactory.getLogger(getClass)

  def apply()(implicit request: Request[AnyContent]): FleetParams = {
    new FleetParams(vehicleRefinedSearchForm.bindFromRequest)
  }


}