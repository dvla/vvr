package uk.gov.dvla.services.enquiry.vehicle.resources

import org.scalatest._
import uk.gov.dvla.services.enquiry.vehicle.jdbi.{VehicleRefinedSearchDao, VehicleDao}
import uk.gov.dvla.domain.{LicensingType, FleetFilter, FleetHeader, Vehicle}
import java.util.Date
import org.mockito.Mockito.{mock, when}
import org.mockito.Matchers._
import uk.gov.dvla.services.exception.NotFoundDataException
import org.joda.time.{DateMidnight, DateTime}
import uk.gov.dvla.time.TimeUtils
import org.skife.jdbi.v2.DBI
import scala.collection.mutable.{Map => MutableMap}
import uk.gov.dvla.domain.criteria.FilterCriteria
import uk.gov.dvla.services.common.FilterCriteriaParams


class VehicleResourceSpec extends FlatSpec with Matchers {

  private val vehicleDao = mock(classOf[VehicleDao])
  private val currentDate = new DateTime(2014, 6, 23, 14, 52, 30)
  private val dbi: DBI = mock(classOf[DBI])
  private val vehicleRefinedSearchDao: VehicleRefinedSearchDao = mock(classOf[VehicleRefinedSearchDao])


  private val timer = new TimeUtils {
    override def currentDateMidnight: DateMidnight = currentDate.toDateMidnight

    override def currentTime: Date = currentDate.toDate

    override def currentDateTime: DateTime = currentDate
  }

  private val vehicle1 = new Vehicle("WSXZAQ2", "Vauxhall", timer.currentTime, "123456", "38", "2","Private Light Goods (PLG)", "red", "2 Axle rigid body", "2 Door Saloon", LicensingType.LICENSED)
  private val vehicle2 = new Vehicle("AA9AQ2", "Vauxhall", timer.currentTime, "123456", "38", "2", "Private Light Goods (PLG)", "red", "2 Axle rigid body", "2 Door Saloon", LicensingType.LICENSED)


  it should "throw NotFoundDataException when trying to get a vehicle with non-existing registration number" in {
    when(vehicleDao.findByRegistrationNumber("QWASE23")).thenReturn(None)
    a [NotFoundDataException] should be thrownBy {
      new VehicleResource(vehicleDao, timer, vehicleRefinedSearchDao, dbi).findVehicleByRegistrationNumber("QWASE23")
    }
  }

  "Get a vehicle with existing registration number" should "return an existing Vehicle object" in {
    val vehicle = vehicle1
    when(vehicleDao.findByRegistrationNumber("WSXZAQ2")).thenReturn(Some(vehicle))
    val vehicleResource = new VehicleResource(vehicleDao, timer, vehicleRefinedSearchDao, dbi)

    vehicleResource.findVehicleByRegistrationNumber("WSXZAQ2") should be (vehicle)
  }


  "Get fleet header with counts for existing fleet number with vehicles" should "return FleetHeader with counts greater than 0" in {
    val fleetNumber = "123456"
    val expectedResult = FleetHeader(7, 5, 3)

    // set up mock object
    when(vehicleDao.countVehiclesByFleetNumber(fleetNumber)).thenReturn(Some(7))
    when(vehicleDao.countOldVehiclesByFleetNumber(fleetNumber)).thenReturn(Some(5))
    when(vehicleDao.countLiableVehiclesByFleetNumber(fleetNumber)).thenReturn(Some(3))

    new VehicleResource(vehicleDao, timer, vehicleRefinedSearchDao, dbi).getFleetHeaderData(fleetNumber) should be (expectedResult)
  }

  "Get fleet header with counts for non-existing fleet number" should "throw NotFoundDataException" in {
    val fleetNumber = "qwerty"
    when(vehicleDao.countVehiclesByFleetNumber(fleetNumber)).thenReturn(None)
    a [NotFoundDataException] should be thrownBy {
      new VehicleResource(vehicleDao, timer, vehicleRefinedSearchDao, dbi).getFleetHeaderData(fleetNumber)
    }
  }

  "Get fleet header with counts for existing fleet number without vehicles" should "return FleetHeader counts of 0" in {
    val fleetNumber = "123098"
    val expectedResult = FleetHeader(0, 0, 0)

    // set up mock object
    when(vehicleDao.countVehiclesByFleetNumber(fleetNumber)).thenReturn(Some(0))
    when(vehicleDao.countOldVehiclesByFleetNumber(fleetNumber)).thenReturn(Some(0))
    when(vehicleDao.countLiableVehiclesByFleetNumber(fleetNumber)).thenReturn(Some(0))

    new VehicleResource(vehicleDao, timer, vehicleRefinedSearchDao, dbi).getFleetHeaderData(fleetNumber) should be (expectedResult)
  }


  "List vehicles with due liability date for given fleet number" should "return a list of Vehicles" in {
    val listOfVehicles: List[Vehicle] = List(
      vehicle1, vehicle2
    )

    when(vehicleRefinedSearchDao.refineVehicleCount(any(classOf[FilterCriteriaParams]))).thenReturn(5)
    when(vehicleRefinedSearchDao.refineVehicleSearch(any(classOf[FilterCriteriaParams]),anyInt(),anyInt())).thenReturn(listOfVehicles)
    val vehicleResource = new VehicleResource(vehicleDao, timer, vehicleRefinedSearchDao, dbi)

    val vehicleList = vehicleResource.findVehiclesByFleet("123456", 0, 2, Map("filter" -> FleetFilter.DUE))

    vehicleList.found should be (5)
    vehicleList.vehicles.size should be (2)
  }


  "List vehicles with due liability date for given non-existing fleet number" should "return an empty list" in {
   val listOfVehicles: List[Vehicle] = List()

    when(vehicleRefinedSearchDao.refineVehicleCount(any(classOf[FilterCriteriaParams]))).thenReturn(0)
    when(vehicleRefinedSearchDao.refineVehicleSearch(any(classOf[FilterCriteriaParams]),anyInt(),anyInt())).thenReturn(listOfVehicles)
    val vehicleResource = new VehicleResource(vehicleDao, timer, vehicleRefinedSearchDao, dbi)

    val vehicleList = vehicleResource.findVehiclesByFleet("wrong", 0, 2, Map("filter" -> FleetFilter.DUE))

    vehicleList.found should be (0)
    vehicleList.vehicles should be (Nil)
  }

  "List vehicles with old age for given fleet number" should "return a list of Vehicles" in {
    val listOfVehicles: List[Vehicle] = List(
      vehicle1, vehicle2
    )
    when(vehicleRefinedSearchDao.refineVehicleCount(any(classOf[FilterCriteriaParams]))).thenReturn(3)
    when(vehicleRefinedSearchDao.refineVehicleSearch(any(classOf[FilterCriteriaParams]),anyInt(),anyInt())).thenReturn(listOfVehicles)
    val vehicleResource = new VehicleResource(vehicleDao, timer, vehicleRefinedSearchDao, dbi)

    val vehicleList = vehicleResource.findVehiclesByFleet("123456", 0, 2, Map("filter" -> FleetFilter.OLD))

    vehicleList.found should be (3)
    vehicleList.vehicles.size should be (2)
  }

  "List vehicles with old age for given non-existing fleet number" should "return an empty list" in {
    val listOfVehicles: List[Vehicle] = List()
    when(vehicleRefinedSearchDao.refineVehicleCount(any(classOf[FilterCriteriaParams]))).thenReturn(0)
    when(vehicleRefinedSearchDao.refineVehicleSearch(any(classOf[FilterCriteriaParams]),anyInt(),anyInt())).thenReturn(listOfVehicles)
    val vehicleResource = new VehicleResource(vehicleDao, timer, vehicleRefinedSearchDao, dbi)

    val vehicleList = vehicleResource.findVehiclesByFleet("wrong", 0, 2, Map("filter" -> FleetFilter.DUE))

    vehicleList.found should be (0)
    vehicleList.vehicles should be (Nil)
  }
}
