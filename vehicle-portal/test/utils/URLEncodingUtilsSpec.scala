package utils

import org.scalatest.{Matchers, FlatSpec}


class URLEncodingUtilsSpec extends FlatSpec with Matchers {

  "encode and then decode operation" should "return the same string before encode operation" in {
    val plainString = "AT/BE+2F-(TR)"

    val encoded = URLEncodingUtils.encode(plainString)

    URLEncodingUtils.decode(encoded) should be(plainString)
  }

  "encoded string" should "not contain url reserved characters except from %" in {
    val reserved = Array("!", "*", "'", "(", ")", ";", ":", "@", "&", "=", "+", "$", ",", "/", "?", "#", "[", "]")

    val encoded = URLEncodingUtils.encode("AT/BE%TY()+-")

    assert(!reserved.exists(encoded.contains(_)))
  }

}
