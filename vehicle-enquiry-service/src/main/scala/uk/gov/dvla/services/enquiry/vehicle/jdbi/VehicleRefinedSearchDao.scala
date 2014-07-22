package uk.gov.dvla.services.enquiry.vehicle.jdbi

import org.skife.jdbi.v2.{Query, Handle, DBI}
import uk.gov.dvla.domain.Vehicle
import uk.gov.dvla.services.enquiry.vehicle.jdbi.VehicleDao.{SELECT_COLUMNS, FROM_VEHICLE}
import uk.gov.dvla.services.enquiry.vehicle.jdbi.VehicleRefinedSearchDao.SELECT_COUNT
import scala.collection.JavaConverters._
import org.slf4j.LoggerFactory
import org.skife.jdbi.v2.util.IntegerMapper
import uk.gov.dvla.services.enquiry.vehicle.sql.FilterCriteriaSqlBuilder
import uk.gov.dvla.services.common.FilterCriteriaParams


class VehicleRefinedSearchDao(dbi:DBI) {

  private val logger = LoggerFactory.getLogger(getClass)

  def refineVehicleSearch(criteria: FilterCriteriaParams, offset :Int, limit :Int): List[Vehicle] = {

    logger.debug("Attempting to open & get Handle")
    val handle: Handle = dbi.open()

    try {
      logger.debug("Got Handle: " + handle)

      val sqlBuilder = FilterCriteriaSqlBuilder(criteria)

      val query =
        SELECT_COLUMNS + FROM_VEHICLE +
        sqlBuilder.buildWhere +
        sqlBuilder.buildOrderBy +
        buildOffsetLimit(offset, limit)

      logger.debug("creating query: "+query)
      val queryMap: Query[java.util.Map[String, Object]] = handle.createQuery(query)

      val criteriaMap = sqlBuilder.flattenToSqlBindingMap

      logger.debug("Attempting to bind Map")
      val qQuery: Query[Vehicle] = queryMap
        .bindFromMap(criteriaMap.asJava)
        .bind("offset", offset)
        .bind("limit", limit)
        .map(new VehicleMapper())

      logger.debug("executing query: "+qQuery.toString)
      val vehicles: java.util.List[Vehicle] = qQuery.list()

      logger.debug("Got list of "+vehicles.size+" Vehicles")
      vehicles.asScala.toList

    } finally {
      handle.close()
    }
  }

  def refineVehicleCount(criteria: FilterCriteriaParams): Int = {

    logger.debug("Attempting to open & get Handle")
    val handle: Handle = dbi.open()

    try {
      logger.debug("Got Handle: "+handle)

      val sqlBuilder = FilterCriteriaSqlBuilder(criteria)

      val query =
        SELECT_COUNT +
        FROM_VEHICLE +
        sqlBuilder.buildWhere

      logger.debug("creating query: " + query)
      val queryMap: Query[java.util.Map[String, Object]] = handle.createQuery(query)

      val criteriaMap = sqlBuilder.flattenToSqlBindingMap

      logger.debug("Attempting to bind Map")
      val qQuery :Query[Integer] = queryMap.bindFromMap(criteriaMap.asJava).map(IntegerMapper.FIRST)

      logger.debug("executing query: "+qQuery.toString)
      val count = qQuery.first()

      logger.debug("Got count of Vehicles: " + count)
      count

    } finally {
      handle.close()
    }

  }

  private def buildOffsetLimit(offset:Int, limit:Int) = {
    val query = new StringBuilder
    query.append(" offset :offset")

    //don't append limit statement in sql query when limit variable equals 0, which means no pagination
    if (limit != 0) {
      query.append(" limit :limit")
    }
    query.toString()
  }
}

object VehicleRefinedSearchDao {

  final val SELECT_COUNT = """
    select
      count(*) """

}
