package uk.gov.dvla.services.filter.vehicle.resources

import com.yammer.metrics.annotation.Timed
import org.slf4j.LoggerFactory
import uk.gov.dvla.services.enquiry.VehicleEnquiry
import javax.ws.rs._
import uk.gov.dvla.services.exception.{NotFoundDataException, NotMatchingDataException}
import uk.gov.dvla.utils.InputFormatUtils._
import uk.gov.dvla.domain.{FleetFilter, VehicleList, Vehicle, FleetHeader}
import com.wordnik.swagger.annotations._
import javax.ws.rs.Path
import uk.gov.dvla.services.common.{QueryMap, PageParams, FilterCriteriaParams}


@Path("/vehicles")
@Api(value = "/vehicles", description = "Vehicle Filter service operations")
@Produces(Array("application/json"))
class VehicleFilterResource(client: VehicleEnquiry) {

  private val logger = LoggerFactory.getLogger(getClass)

  @GET
  @ApiOperation(value = "Fetch a vehicle", notes = "Finds one vehicle", response = classOf[Vehicle])
  @ApiResponses(value = Array(new ApiResponse(code = 200, message = "Returns a vehicle with specified registration number"),
    new ApiResponse(code = 404, message = "Returns an error message, if no vehicle found for the input criteria")))
  @Timed
  def findVehicle(@QueryParam("fleetNumber") fleetNumber: String,
                  @QueryParam("registrationNumber") registrationNumber: String,
                  @QueryParam("make") make: String): Vehicle = {

    val vehicle = client.getVehicle(convertRegistrationNumberToUnifiedFormat(registrationNumber)).getOrElse({
      logger.info(s"Vehicle with requested registration number could not be found. Fleet: $fleetNumber, registration nr: $registrationNumber, make: $make")
      throw NotFoundDataException("Vehicle with requested registration number could not be found")
    })

    if (!equalMakes(vehicle.make, make)) {
      logger.info(s"Vehicle with requested registration number: $registrationNumber and make: $make could not be found")
      throw NotFoundDataException("Vehicle with requested registration number and make could not be found")
    }

    if (!equalFleetNumbers(vehicle.fleetNumber, fleetNumber)) {
      logger.info(s"Vehicle with registration number: $registrationNumber is not part of a fleet: $fleetNumber")
      throw NotMatchingDataException("Vehicle is not part of a fleet")
    }

    logger.debug(s"Retrieved vehicle: $vehicle")
    vehicle
  }

   @GET
  @Path("/fleet/header/{fleetNumber}")
  @ApiOperation(value = "Returns data for alerts in the fleet list view", response = classOf[FleetHeader])
  @ApiResponses(value = Array(new ApiResponse(code = 200, message = "Returns a FleetHeader"),
    new ApiResponse(code = 404, message = "not found")))
  @Timed
  def getFleetHeaderData(@PathParam("fleetNumber") fleetNumber: String): FleetHeader = {
    client.getFleetHeaderData(fleetNumber).getOrElse({
      logger.info(s"There's no such a fleet number. Fleet: $fleetNumber")
      throw NotFoundDataException("There's no such a fleet number")
    })
  }


  @GET
  @Path("/fleetRecords/{fleetNumber}")
  @ApiOperation(value = "Fetches vehicles from a fleet meeting offset, limit and filter criteria", response = classOf[VehicleList])
  @ApiResponses(value = Array(new ApiResponse(code = 200, message = "Returns list of vehicles for a fleet and the number found for the given filter criteria. If there are no vehicles in the fleet returns an empty list.")))
  @Timed
  def findVehiclesByFleet(@PathParam("fleetNumber") fleetNumber: String,
                          @QueryParam("offset") offset: Int,
                          @QueryParam("limit") limit: Int,
                          @QueryMap filterCriteria: Map[String, String]) : VehicleList = {



    val criteria = FilterCriteriaParams(fleetNumber, filterCriteria)
    logger.error("criteria: "+ criteria)

    val result: VehicleList = client.getVehiclesByFleet(
      fleetNumber,
      offset,
      limit,
      criteria
    )

    logger.debug("findVehiclesByFleet Searched Vehicles :"+result.vehicles.size)
    result
   }

}