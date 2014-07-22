package utils

import play.api.mvc.{SimpleResult, Session}
import play.api.data.Form
import model.{VehicleRefinedSearchData, VehicleData}
import EncryptionUtilsHelper._
import uk.gov.dvla.domain.FleetFilter
import common.exception.SessionException

object SessionUtils {

  val DEFAULT_LIMIT = 25
  val MAX_LIMIT = 100

  implicit class RichSession(session: Session) {

    def fleetNumber() = getDecOrThrow("fleetNumber")

    def registrationNumber() = getDecOrThrow("registrationNumber")

    def vehicleMake() = getDecOrThrow("vehicleMake")

    def offset = getDecOrDefault("offset", "0").toInt

    def limit = getDecOrDefault("limit", DEFAULT_LIMIT.toString).toInt.min(MAX_LIMIT)

    def maxOffset = getDecOrDefault("maxOffset", "0").toInt

    def backToFleetPage = getDecOrDefault("backToFleetPage", "0").toInt

    def error = getDecOrDefault("error", "")

    def errors = if (getDecOrDefault("error", null) == null) Nil else List(error)

    def bindToForm(form: Form[VehicleData]) = form.bind(dec(session.data))

    def withFleetNumber(fleetNumber: String) = session + enc("fleetNumber" -> fleetNumber)

    def withRegistrationNumber(registrationNumber: String) = session + enc("registrationNumber" -> registrationNumber)

    def withVehicleMake(vehicleMake: String) = session + enc("vehicleMake" -> vehicleMake)

    def withBackToFleetPage(backToFleetPage: Int) = session + enc("backToFleetPage" -> backToFleetPage.toString)

    def regNoFilter = getDecOrDefault("regNoFilter","")

    def makeFilter = getDecOrDefault("makeFilter","")

    def withVehicleData(v: VehicleData) = session.
      withFleetNumber(v.fleetNumber).
      withRegistrationNumber(v.registrationNumber).
      withVehicleMake(v.vehicleMake)

    def withVehicleData(fleetNumber: String, registrationNumber: String, vehicleMake: String) = session.
      withFleetNumber(fleetNumber).
      withRegistrationNumber(registrationNumber).
      withVehicleMake(vehicleMake)

    def withPaging(offset: Int, limit: Int, maxOffset: Int) = session +
      enc("offset" -> Math.max(0, offset).toString) +
      enc("limit" -> Math.max(0, limit).toString) +
      enc("maxOffset" -> Math.max(0, maxOffset).toString)

    def withFilter(filter: String) = session +
      enc("filter" -> filter)

    def withError(msg: String) = session + enc("error" -> msg)

    private def getDecOrThrow(key: String) =
      (session.get(enc(key)) map dec).getOrElse(throw SessionException(s"missing $key in session"))

    private def getDecOrDefault(key: String, default: String) =
      (session.get(enc(key)) map dec).getOrElse(default)


  }

  implicit class RichResult(result: SimpleResult) {

    def withError(msg: String) = result.withSession(Session().withError(msg))

  }

}