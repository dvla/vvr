package utils

import SessionUtils._
import play.api.mvc.Session
import play.api.test.Helpers._
import org.scalatest.{Matchers, FlatSpec}
import play.api.test.FakeApplication
import EncryptionUtilsHelper._
import common.exception.SessionException

class SessionUtilsSpec extends FlatSpec with Matchers {

  val secret = "ABCDEFGHIJKLIMOPQRSTUVWXYZ"

  "SessionUtils.fleetNumber" should "return the value stored under 'fleetNumber' key" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> secret))) {
      val sess = new Session(Map(
        enc("fleetNumber" -> "123456")
      ))

      sess.fleetNumber should be("123456")
    }
  }

  "SessionUtils.registrationNumber" should "return the value stored under 'registrationNumber' key" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> secret))) {
      val sess = new Session(Map(
        enc("registrationNumber" -> "AAA123B")
      ))

      sess.registrationNumber should be("AAA123B")
    }
  }

  "SessionUtils.vehicleMake" should "return the value stored under 'vehicleMake' key" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> secret))) {
      val sess = new Session(Map(
        enc("vehicleMake" -> "SKODA")
      ))

      sess.vehicleMake should be("SKODA")
    }
  }

  "some SessionUtils methods" should "throw SessionException if key is not found" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> secret))) {
      val sess = new Session()
      a[SessionException] should be thrownBy (sess.fleetNumber)
      a[SessionException] should be thrownBy (sess.registrationNumber)
      a[SessionException] should be thrownBy (sess.vehicleMake)
    }
  }

  "other SessionUtils methods" should "return defaults when key is not found" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> secret))) {
      val sess = new Session()
      sess.backToFleetPage should be(0)
      sess.offset should be(0)
      sess.limit should be(SessionUtils.DEFAULT_LIMIT)
    }
  }

  "SessionUtils.withVehicleData" should "populate the session with fleet number, reg and make" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> secret))) {
      val sess = new Session().withVehicleData("123456", "AAA123B", "SKODA")

      sess.fleetNumber should be("123456")
      sess.registrationNumber should be("AAA123B")
      sess.vehicleMake should be("SKODA")
    }
  }

  "SessionUtils.withBackToFleetPage" should "populate the session with integer value" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> secret))) {
      val sess = new Session().withBackToFleetPage(23)

      sess.backToFleetPage should be(23)
    }
  }

  "SessionUtils.withPaging" should "populate offset, limit and maxOffset" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> secret))) {
      val sess = new Session().withPaging(10, 100, 1000)

      sess.offset should be(10)
      sess.limit should be(100)
      sess.maxOffset should be(1000)
    }
  }

  "SessionUtils.errors" should "return a list if error is set" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> secret))) {
      val sess = new Session().withError("whoops")

      sess.errors should be(List("whoops"))
    }
  }

  "SessionUtils.errors" should "return Nil if error is not set" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> secret))) {
      val sess = new Session()

      sess.errors should be(Nil)
    }
  }

}
