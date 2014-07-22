package uk.gov.dvla.services.exception

import javax.ws.rs.core.Response
import javax.ws.rs.WebApplicationException


case class NotFoundDataException(response: Response) extends WebApplicationException(response)

object NotFoundDataException {

  def apply(message: String): NotFoundDataException =
    NotFoundDataException(Response.status(Response.Status.NOT_FOUND).entity(message).build())

  def apply(): NotFoundDataException =
    NotFoundDataException(Response.status(Response.Status.NOT_FOUND).build())

}
