package utils

import org.scalatest.{Matchers, FlatSpec}
import play.api.data.FormError


class FormUtilsSpec extends FlatSpec with Matchers {

  val actualFormErrors = Seq(
    FormError("a", "empty field", Nil),
    FormError("a", "invalid field", Nil),
    FormError("c", "empty field", Nil),
    FormError("b", "empty field", Nil),
    FormError("b", "invalid field", Nil)
  )

  val expectedFormErrors = Seq(
    FormError("a", "empty field", Nil),
    FormError("c", "empty field", Nil),
    FormError("b", "empty field", Nil)
  )

  "getFirstErrors" should "filter appropriate errors and not change the order of the collection" in {
    FormUtils.getFirstErrors(actualFormErrors) should be(expectedFormErrors)
  }

}
