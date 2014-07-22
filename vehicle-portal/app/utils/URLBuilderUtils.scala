package utils

import java.net.URLEncoder


object URLBuilderUtils {

  def buildQueryParams(params: Map[String, Option[String]]): String = {
    val query = params.filter(_._2.isDefined).map(p => p._1 + "=" + URLEncoder.encode(p._2.get, "utf-8")).mkString("&")
    query match {
      case "" => query
      case _ => "?" + query
    }
  }

  def buildUrl(baseUrl: String, queryString: Map[String, Seq[String]]): String = {

    val qs = Option(queryString).filterNot(_.isEmpty).map {
      params =>
        params.toSeq.flatMap {
          pair =>
            pair._2.map(value => (pair._1 + "=" + URLEncoder.encode(value, "utf-8")))
        }.mkString("&")
    }.getOrElse("")

    if (qs.isEmpty)
      baseUrl
    else if (baseUrl.contains("?"))
      baseUrl + "&" + qs
    else
      baseUrl + "?" + qs
  }

}
