package utils

import java.net.{URLEncoder, URLDecoder}


object URLEncodingUtils {

  val STRING_ENCODING = "UTF-8"

  def encode(param: String): String = {
    URLEncoder.encode(param, STRING_ENCODING)
  }

  def decode(param: String): String = {
    URLDecoder.decode(param, STRING_ENCODING)
  }

}
