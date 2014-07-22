package uk.gov.dvla.domain

object FleetFilter {
  val ALL = "all"
  val DUE = "due"
  val OLD = "old"
  val CUSTOM = "custom"

  def validateFilter(filter: String) = {
    val checkedFilter: String = filter match {
      case DUE => DUE
      case OLD => OLD
      case _ => ALL
    }
    checkedFilter
  }
}
