package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Redirect(routes.Search.searchForm())
  }

  def dummyVehicleList = Action {
    Ok(views.html.vehicleList())
  }

  def logout = Action {
    implicit request => {
      val redirectUrl =
      Play.current.configuration.getString("logout.url").getOrElse("https://gov.uk")
      Redirect(redirectUrl, 303).withNewSession
    }
  }


}