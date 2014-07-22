package uk.gov.dvla.services.enquiry.vehicle.resources

import com.yammer.metrics.annotation.Timed
import javax.ws.rs._
import uk.gov.dvla.services.enquiry.vehicle.jdbi.{VehicleRefinedSearchDao, VehicleDao}
import com.wordnik.swagger.annotations.{Api, ApiResponse, ApiResponses, ApiOperation}
import uk.gov.dvla.utils.InputFormatUtils._
import uk.gov.dvla.domain.{VehicleList, FleetFilter, FleetHeader, Vehicle}
import uk.gov.dvla.services.exception.NotFoundDataException
import uk.gov.dvla.time.TimeUtils
import org.slf4j.LoggerFactory
import org.skife.jdbi.v2.DBI
import uk.gov.dvla.services.common.{FilterCriteriaParams, QueryMap}

@Path("/vehicles")
@Api(value = "/vehicles", description = "Vehicle Enquiry service operations")
@Produces(Array("application/json"))
class VehicleResource(val dao: VehicleDao, val timer: TimeUtils, val vehicleRefinedSearchDao: VehicleRefinedSearchDao, val dbi: DBI) {

  private val logger = LoggerFactory.getLogger(getClass)

  @GET
  @Path("/{registrationNumber}")
  @ApiOperation(value = "Fetch a vehicle by registration number", notes = "Fetches one vehicle from the database for the given registration number", response = classOf[Vehicle])
  @ApiResponses(value = Array(new ApiResponse(code = 200, message = "returns a Vehicle"),
    new ApiResponse(code = 404, message = "not found")))
  @Timed
  def findVehicleByRegistrationNumber(@PathParam("registrationNumber") registrationNumber: String): Vehicle = {
    dao.findByRegistrationNumber(convertRegistrationNumberToUnifiedFormat(registrationNumber)).
      getOrElse({
        logger.info(s"No vehicle for registration number $registrationNumber throwing NotFoundDataException")
        throw NotFoundDataException()
      })
  }

  @GET
  @Timed
  @Path("/fleet/{fleetNumber}")
  @ApiOperation(value = "Lists all vehicles for a fleet", notes = "Applies a filter and paging offset/limit parameters", response = classOf[VehicleList])
  @ApiResponses(value = Array(new ApiResponse(code = 200, message = "Returns a list of vehicles for a fleet")))
  def findVehiclesByFleet(@PathParam("fleetNumber") fleetNumber: String,
                          @QueryParam("offset") offset: Int,
                          @QueryParam("limit") limit: Int,
                          @QueryMap requestParams: Map[String, String]) : VehicleList = {

    logger.debug("In the findVehiclesByFleet")
    val criteria = FilterCriteriaParams(fleetNumber, requestParams)

    val found = vehicleRefinedSearchDao.refineVehicleCount(criteria)
    val vehicles = vehicleRefinedSearchDao.refineVehicleSearch(criteria, offset, limit)

    VehicleList(found, vehicles)
  }


  @GET
  @Path("/fleet/header/{fleetNumber}")
  @ApiOperation(value = "Returns data for alerts in fleet list view", response = classOf[FleetHeader])
  @ApiResponses(value = Array(new ApiResponse(code = 200, message = "returns a Fleet Header"),
    new ApiResponse(code = 404, message = "not found")))
  @Timed
  def getFleetHeaderData(@PathParam("fleetNumber") fleetNumber: String): FleetHeader = {

    val vehicleCount = dao.countVehiclesByFleetNumber(fleetNumber).getOrElse({
      logger.info(s"No vehicle for registration number: $fleetNumber . Throwing NotFoundDataException")
      throw NotFoundDataException()
    })
    val dueLiability = dao.countLiableVehiclesByFleetNumber(fleetNumber).getOrElse(throw NotFoundDataException())
    val oldVehicles = dao.countOldVehiclesByFleetNumber(fleetNumber).getOrElse(throw NotFoundDataException())
    FleetHeader(vehicleCount, oldVehicles, dueLiability)
  }


}