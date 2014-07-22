package controllers

import play.api.mvc.{Controller, Action}
import org.slf4j.LoggerFactory


trait ExceptionController extends Controller {

  val logger = LoggerFactory.getLogger(getClass)

  def sessionError = Action {
    implicit request =>
      logger.info(s"sessionError for ${request.uri} headers: ${request.headers}")
      Ok(views.html.error("views.display.error.session.expired", request.uri)).withNewSession
  }

}

object ExceptionController extends ExceptionController