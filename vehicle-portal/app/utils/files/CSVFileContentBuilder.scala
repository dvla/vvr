package utils.files

import java.io.{InputStream, ByteArrayInputStream}
import java.nio.charset.Charset


object CSVFileContentBuilder extends FileContentBuilder {

  override def create(headerMapper: Seq[(String, String, Option[Formatter])], records: Seq[AnyRef], charset: Charset): InputStream = {
    val content = header(headerMapper) +
      records.map {
        (record) =>
          headerMapper.map {
            (f) => fieldValue(record, f._1, f._3)
          }.mkString(",")
      }.mkString("\n")
    new ByteArrayInputStream(content.getBytes(charset))
  }

  private def header(headers: Seq[(String, String, Option[Formatter])]): String = {
    headers.map(_._2).mkString(",") + "\n"
  }

}
