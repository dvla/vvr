package uk.gov.dvla.services.enquiry.reference.resources

import com.yammer.metrics.annotation.Timed
import javax.ws.rs.{GET, Path, Produces}
import com.wordnik.swagger.annotations.{ApiResponse, ApiResponses, Api, ApiOperation}
import uk.gov.dvla.services.enquiry.reference.jdbi.ReferenceEnquiryDao
import scala.collection.JavaConverters._


@Path("/reference")
@Api(value = "/reference", description = "Reference Data operations for Vehicle Enquiries")
@Produces(Array("application/json"))
class ReferenceEnquiryResource(val dao: ReferenceEnquiryDao) {

  @GET
  @Path("/makes")
  @ApiOperation(value = "Find Makes", notes = "Find all makes of vehicles", response = classOf[String])
  @ApiResponses(value = Array(new ApiResponse(code = 200, message = "Returns an empty JSON, if no makes found"),
    new ApiResponse(code = 200, message = "Returns a non-empty JSON, if makes are found")))
  @Timed
  def listMakes(): List[String] = {
    dao.findMakes().asScala.toList
  }


}