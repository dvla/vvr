package forms

import play.api.data.Form
import play.api.data.Forms._
import model.VehicleData
import uk.gov.dvla.utils.InputFormatUtils._


trait VehicleSearchForm {

  val vehicleForm = Form(
    mapping(
      "fleetNumber" -> text.verifying("vvrform.enter.your.fleet.number", !_.isEmpty)
        .verifying("vvrform.enter.a.valid.fleet.number", hasValidFleetNumberFormat _),
      "registrationNumber" -> text.verifying("vvrform.enter.your.vehicle.registration.number", !_.isEmpty)
        .verifying("vvrform.enter.a.valid.vehicle.registration.number", hasValidRegistrationNumberFormat _),
      "vehicleMake" -> text.verifying("vvrform.enter.your.vehicle.make", !_.isEmpty)
        .verifying("vvrform.enter.a.valid.vehicle.make", hasValidMakeFormat _)
    )(VehicleData.apply)(VehicleData.unapply)
  )
}