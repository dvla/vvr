package uk.gov.dvla.services.enquiry.vehicle.resources

import com.yammer.dropwizard.testing._
import org.junit.Test
import uk.gov.dvla.services.enquiry.vehicle.jdbi.{VehicleRefinedSearchDao, VehicleDao}
import org.mockito.Mockito._
import com.sun.jersey.api.client.ClientResponse
import org.joda.time.{DateTime, DateMidnight}
import java.util.Date
import uk.gov.dvla.time.TimeUtils
import org.skife.jdbi.v2.DBI

class VehicleResourceDropwizardTest extends ResourceTest {

  private val vehicleDao: VehicleDao = mock(classOf[VehicleDao])
  private val currentDate = new DateTime(2014, 6, 23, 14, 52, 30)
  private val dbi: DBI = mock(classOf[DBI])
  private val vehicleRefinedSearchDao: VehicleRefinedSearchDao = mock(classOf[VehicleRefinedSearchDao])

  private val timer = new TimeUtils {
    override def currentDateMidnight: DateMidnight = currentDate.toDateMidnight

    override def currentTime: Date = currentDate.toDate

    override def currentDateTime: DateTime = currentDate
  }

  override def setUpResources {
    when(vehicleDao.findByRegistrationNumber("QQQQQ22")).thenReturn(None)
    addResource(new VehicleResource(vehicleDao, timer, vehicleRefinedSearchDao, dbi))
  }

  @Test
  def shouldReturnNotFoundWhenFindByRegistrationNumber {
    assert(client.resource("/vehicle/TTTTT33").get(classOf[ClientResponse]).getStatus == 404)
  }

}
