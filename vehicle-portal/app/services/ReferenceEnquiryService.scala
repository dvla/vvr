package services

import play.api.Play
import play.api.libs.ws.WS
import play.api.libs.json.{JsValue, Json}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import play.api.http.Status._


trait ReferenceEnquiryService {
  def makes: Future[JsValue]
}

object ReferenceEnquiryService extends ReferenceEnquiryService {

  val baseUrl = Play.current.configuration.getString("referenceEnquiryService.url").get

  def makes: Future[JsValue] = {
    val url = baseUrl + "reference/makes/"
    WS.url(url).get.map {
      response =>
        response.status match {
          case OK => response.json
          case _ => Json.parse("[]")
        }
    }
  }

}
