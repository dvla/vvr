package utils

import play.api.data.FormError


object FormUtils {

  /*
   * Get first form error per field insuring the order of fields in a form
   */
  def getFirstErrors(errors: Seq[FormError]): Seq[FormError] =
    errors //                                                                    -> Seq(FormError)
      .zipWithIndex  //we zip each error with an index which we will use to sort -> List(FormError -> Int)
      .groupBy(_._1.key) //we group the errors by the keys of the fields         -> Map(FormError.key -> Seq(FormError ->  Int))
      .map(_._2.head) //We get from each group the first error                   -> List(FormError -> Int)
      .toSeq          //                                                         -> Seq(FormError -> Int)
      .sortWith(_._2 < _._2) // we sort by the saved earlier index to maintain order
      .map(_._1)      // we take only the form errors                            -> Seq(FormError)
  ;

  /**
   *
   * @param types should be one of DateFieldSuffix
   * @param separator "_" or "." or anything else if you would like
   */
  def dateFieldWithSuffix(types:String, separator:String = "_"):String = {
    return s"$separator$types"
  }
}

object DateFieldSuffix {
  val MONTH_FROM = "monthFrom"
  val YEAR_FROM = "yearFrom"
  val MONTH_TO = "monthTo"
  val YEAR_TO = "yearTo"
  val MONTH = "month"
  val YEAR = "year"
}