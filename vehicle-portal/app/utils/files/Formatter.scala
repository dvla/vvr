package utils.files

import java.util.Date
import java.text.SimpleDateFormat


trait Formatter {

  def apply(f: Any): String

}

object DateFormatter extends Formatter {
  def apply(f: Any): String = {
    val date = f.asInstanceOf[Date]
    new SimpleDateFormat("yyyy-MM-dd").format(date)
  }
}

object QuoteSurroundFormatter extends Formatter {
  def apply(f: Any): String = {
    "\"" + f.asInstanceOf[String] + "\""
  }
}

