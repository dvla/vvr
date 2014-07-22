package utils.files

import java.io.InputStream
import java.nio.charset.Charset


trait FileContentBuilder {

  def create(headerMapper: Seq[(String, String, Option[Formatter])], records: Seq[AnyRef], charset: Charset): InputStream

  def fieldValue(anyRefObject: AnyRef, fieldName: String, formatter: Option[Formatter]): String = {
    val method = anyRefObject.getClass.getMethod(fieldName)
    val fieldValue = method.invoke(anyRefObject)

    (fieldValue, formatter) match {
      case (null, _) | (None, _) => ""
      case (Some(value), None) => value.toString
      case (_, None) => fieldValue.toString
      case (Some(value), Some(_)) => formatter.get.apply(value)
      case (_, Some(_)) => formatter.get.apply(fieldValue)
    }
  }

}
