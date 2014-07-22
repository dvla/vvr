package uk.gov.dvla.services.exception

import javax.ws.rs.core.Response
import javax.ws.rs.WebApplicationException


case class NotMatchingDataException(response: Response) extends WebApplicationException(response)

object NotMatchingDataException {

  def apply(message: String): NotMatchingDataException =
    NotMatchingDataException(Response.status(422).entity(message).build())

}