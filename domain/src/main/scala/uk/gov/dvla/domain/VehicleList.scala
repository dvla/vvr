package uk.gov.dvla.domain

/**
 * A list of vehicles with count of how many in total are found.
 *
 * This is used to pass a page of vehicle query results between services
 */
case class VehicleList(found:Int, vehicles:List[Vehicle])

object VehicleList{
  val empty = VehicleList(0,Nil)
}
