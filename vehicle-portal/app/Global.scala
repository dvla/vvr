import common.exception.SessionException
import controllers.routes
import org.slf4j.LoggerFactory
import play.api.mvc._
import play.api.mvc.SimpleResult
import play.api.mvc.Results._
import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global


object Global extends WithFilters(NoCacheFilter) {

  private val logger = LoggerFactory.getLogger(getClass)

  override def onError(request: RequestHeader, ex: Throwable): Future[SimpleResult] = {
    Future(ex.getCause match {
      case _: SessionException => {
        logger.debug("Session has expired")
        Redirect(routes.ExceptionController.sessionError)
      }
      case _ => {
        logger.error("Critical application failure", ex)
        InternalServerError(views.html.error("views.display.error.critical", request.uri)).withNewSession
      }
    })
  }

  override def onHandlerNotFound(request: RequestHeader): Future[SimpleResult] = {
    logger.info(s"Route $request was not found")
    Future(NotFound(views.html.error("views.display.error.404.action.not.found", request.uri)))
  }

  override def onBadRequest(request: RequestHeader, error: String): Future[SimpleResult] = {
    logger.info(s"$request - impossible to bind")
    Future(BadRequest(views.html.error("views.display.error.400.badrequest", request.uri)))
  }

}

object NoCacheFilter extends Filter {

  override def apply(f: (RequestHeader) => Future[SimpleResult])(rh: RequestHeader): Future[SimpleResult] = {
    f(rh).map(_.withHeaders("Cache-Control" -> "no-cache, no-store, must-revalidate", "Expires" -> "0"))
  }

}