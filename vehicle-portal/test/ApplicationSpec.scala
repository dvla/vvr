import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.i18n.Messages
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "redirect from the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(SEE_OTHER)
    }

//    "render the searchform" in new WithApplication{
//      val search = route(FakeRequest(GET, "/vehicle-record/search")).get
//
//      status(search) must equalTo(OK)
//      contentType(search) must beSome.which(_ == "text/html")
//      contentAsString(search) must contain (Messages("search.form.title"))
//    }
  }
}
