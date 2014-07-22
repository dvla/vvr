package uk.gov.dvla.services.common

import javax.ws.rs.ext.Provider
import com.sun.jersey.spi.inject.{Injectable, InjectableProvider}
import com.sun.jersey.api.model.Parameter
import javax.ws.rs.core.MultivaluedMap
import com.sun.jersey.core.spi.component.{ComponentContext, ComponentScope}
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable
import com.sun.jersey.api.core.HttpContext


@Provider
class QueryMapProvider extends InjectableProvider[QueryMap, Parameter] {

  override def getScope(): ComponentScope = ComponentScope.PerRequest

  override def getInjectable(ic: ComponentContext, a: QueryMap, c: Parameter): Injectable[Map[String,String]] = {
    if (classOf[Map[String,String]] != c.getParameterClass())
      null
    else
      new MapInjectable(!c.isEncoded)
  }

}

class MapInjectable(decode: Boolean) extends AbstractHttpContextInjectable[Map[String,String]] {

  def getValue(c:HttpContext) = {

    val mvm: MultivaluedMap[String, String] = c.getUriInfo.getQueryParameters
    val map = collection.mutable.Map[String, String]()
    val i = mvm.entrySet().iterator()

    while( i.hasNext() ) {
      val entry = i.next()
      val key = entry.getKey()
      val values = entry.getValue().iterator()
      if( values.hasNext() ) {  // just take first value
        map.put(key, values.next)
      }
    }

    map.toMap
  }
}

