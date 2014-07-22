package controllers

import play.api.test.Helpers._
import play.api.test.{FakeRequest, FakeApplication}
import org.mockito.Mockito._
import play.api.libs.ws.Response
import play.api.libs.json.Json
import services.VehicleFilterService
import play.api.mvc.SimpleResult
import org.scalatest.{Matchers, FlatSpec}
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, ExecutionContext}
import ExecutionContext.Implicits.global
import uk.gov.dvla.domain.FleetFilter
import uk.gov.dvla.time.TimeUtils
import model.VehicleRefinedSearchData
import utils.EncryptionUtilsHelper


class FileExportFleetRecordsSpec extends FlatSpec with Matchers {

  private val secret = "ABCDEFGHIJKLIMOPQRSTUVWXYZ"

  "FileExportFleetRecords.exportFleet" should "return HTTP OK in response" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> secret))) {
      val exportFleetRecordsController = new FileExportFleetRecords() {
        override def vehicleFilterService = mockVehicleFilterService
        override val timer = TimeUtils
      }
      val request = FakeRequest(routes.FileExportFleetRecords.exportFleet()).withSession(
        EncryptionUtilsHelper.enc("fleetNumber" -> "123456")
      )

      val result: Future[SimpleResult] = exportFleetRecordsController.exportFleet.apply(request)
      val readyResult: SimpleResult = Await.result(result, 30 seconds)

      readyResult.header.status should be(OK)
    }
  }

  private def mockVehicleFilterService: VehicleFilterService = {
    val vehicles =
      """[{
          "registrationNumber": "AAB911A",
          "make": "FORD",
          "taxClass": "Diesel",
          "liability": "2014-09-01",
          "motExpiry": "2015-10-02",
          "firstRegistration": "2014-09-01",
          "taxStatus": "SORN",
          "fleetNumber": "123456",
          "co2Emissions": 12,
          "fuelType": "2",
          "taxBand": "12",
          "colour":"white",
          "licensingType":"S",
          "wheelplan": "2 Axle rigid body",
          "bodyType": "2 Door Saloon"
      }]""".stripMargin
    val fleetRecordsResponse = mock(classOf[Response])
    when(fleetRecordsResponse.status).thenReturn(200)
    when(fleetRecordsResponse.json).thenReturn(Json.parse(vehicles))

    val mockVehicleFilterService = mock(classOf[VehicleFilterService])
    when(mockVehicleFilterService.getFleetRecords("123456", 0, 0, VehicleRefinedSearchData(Some(FleetFilter.ALL)))).thenReturn(Future(fleetRecordsResponse))
    mockVehicleFilterService
  }

}
