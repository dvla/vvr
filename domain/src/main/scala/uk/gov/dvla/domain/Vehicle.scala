package uk.gov.dvla.domain

import java.util.{Calendar, Date}
import play.api.libs.json.JsValue
import scala.beans.BeanProperty
import com.fasterxml.jackson.annotation.{JsonProperty, JsonCreator}

@JsonCreator
class Vehicle(
              @BeanProperty @JsonProperty("registrationNumber") val registrationNumber: String,
              @BeanProperty @JsonProperty("make") val make: String,
              @BeanProperty @JsonProperty("firstRegistration") val firstRegistration: Date,
              @BeanProperty @JsonProperty("fleetNumber") val fleetNumber: String,
              @BeanProperty @JsonProperty("taxCode") val taxCode: String,
              @BeanProperty @JsonProperty("taxDescription") val taxDescription: String,
              @BeanProperty @JsonProperty("fuelType") val fuelType: String,
              @BeanProperty @JsonProperty("colour") val colour: String,
              @BeanProperty @JsonProperty("wheelplan") val wheelplan: String,
              @BeanProperty @JsonProperty("bodyType") val bodyType: String,
              @BeanProperty @JsonProperty("licensingType") val licensingType: String,
              @BeanProperty @JsonProperty("liability") val liability: Option[Date] = None,
              @BeanProperty @JsonProperty("cylinderCapacity") val cylinderCapacity: Option[Int] = None,
              @BeanProperty @JsonProperty("co2Emissions") val co2Emissions: Option[Int] = None,
              @BeanProperty @JsonProperty("massInService") val massInService: Option[Int] = None,
              @BeanProperty @JsonProperty("numberOfSeats") val numberOfSeats: Option[Int] = None,
              @BeanProperty @JsonProperty("standingCapacity") val standingCapacity: Option[Int] = None,
              @BeanProperty @JsonProperty("revenueWeight") val revenueWeight: Option[Int] = None,
              @BeanProperty @JsonProperty("firstRegistrationUk") val firstRegistrationUk: Option[Date] = None,
              @BeanProperty @JsonProperty("vehicleCategory") val vehicleCategory: Option[String] = None,
              @BeanProperty @JsonProperty("motExpiry") val motExpiry: Option[Date] = None,
              @BeanProperty @JsonProperty("model") val model: Option[String] = None,
              @BeanProperty @JsonProperty("vin") val vin: Option[String] = None,
              @BeanProperty @JsonProperty("lastV5") val lastV5: Option[Date] = None,
              @BeanProperty @JsonProperty("engineNumber") val engineNumber: Option[String] = None) {

  val taxStatus: Option[String] = Vehicle.getTaxStatus(liability, licensingType)

  val taxBand: Option[String] = Vehicle.getTaxBand(firstRegistration, co2Emissions.getOrElse(0))

  def taxCodeWithDescription: String ={
    s"$taxCode - $taxDescription"
  }
}

object Vehicle {

  def apply(json: JsValue): Vehicle = {
    new Vehicle(
      (json \ "registrationNumber").as[String],
      (json \ "make").as[String],
      (json \ "firstRegistration").as[Date],
      (json \ "fleetNumber").as[String],
      (json \ "taxCode").as[String],
      (json \ "taxDescription").as[String],
      (json \ "fuelType").as[String],
      (json \ "colour").as[String],
      (json \ "wheelplan").as[String],
      (json \ "bodyType").as[String],
      (json \ "licensingType").as[String],
      (json \ "liability").asOpt[Date],
      (json \ "cylinderCapacity").asOpt[Int],
      (json \ "co2Emissions").asOpt[Int],
      (json \ "massInService").asOpt[Int],
      (json \ "numberOfSeats").asOpt[Int],
      (json \ "standingCapacity").asOpt[Int],
      (json \ "revenueWeight").asOpt[Int],
      (json \ "firstRegistrationUk").asOpt[Date],
      (json \ "vehicleCategory").asOpt[String],
      (json \ "motExpiry").asOpt[Date],
      (json \ "model").asOpt[String],
      (json \ "vin").asOpt[String],
      (json \ "lastV5").asOpt[Date],
      (json \ "engineNumber").asOpt[String]
    )
  }

  /*
   * The value for “Tax Status” shall be calculated based on the following:
   *   Where the date of liability for the vehicle is in the past then output = Expired
   *   Where the date of liability for the vehicle is in the future but is less than 12mths then output = Taxed
   *   --Where the date of liability for the vehicle is in the future but is over 12mths then output = SORN
   *   ++Based on US1019 SORN is now calculated from table values
   *
   * The value in 3 above needs to be configurable as it will need to be changed at a later date.
   *
   * @param paramLiability optional liability date - might be empty in DB
   * @param licensingType : value from object -> LicensingType.
   * @return
   */
  def getTaxStatus(paramLiability: Option[Date], licensingType: String): Option[String] = {
    if ((paramLiability.isEmpty) && isSORN(licensingType)) {
      Option(TaxStatus.SORN)
    }
    else if(paramLiability.isEmpty){
      None
    }
    else {
      val liability = paramLiability.get

      var today = Calendar.getInstance()
      today = rollbackDate(today)

      var dateOfLiability = Calendar.getInstance()
      dateOfLiability.setTime(liability)
      dateOfLiability = rollbackDate(dateOfLiability)

      val twelveMonthsInTheFuture = Calendar.getInstance()
      twelveMonthsInTheFuture.add(Calendar.MONTH, 12)

      val startOfNextCalendarMonth = Calendar.getInstance()
      startOfNextCalendarMonth.add(Calendar.MONTH, 1)

      if(isSORN(licensingType)) {
        // Based on US1019 SORN is now calculated from vehicle table values
        Option(TaxStatus.SORN)
      } else if (dateOfLiability.before(today)) {
        //Where the date of liability for the vehicle is in the past then output = Expired
        Option(TaxStatus.EXPIRED)
      }
      else if (! isSORN(licensingType) && dateOfLiability.before(twelveMonthsInTheFuture)) {
        //Where the date of liability for the vehicle is in the future but is less than 12mths then output = Taxed
        if(dateOfLiability.before(startOfNextCalendarMonth)){
          Option(TaxStatus.TAX_DUE)
        }else{
          Option(TaxStatus.TAXED)
        }
      }
      else if (!isSORN(licensingType) && dateOfLiability.after(twelveMonthsInTheFuture)) {
        //US1019, Where the vehicle is not SORN and the date of liability for the vehicle is more than 12 months in the future = Taxed
        Option(TaxStatus.TAXED)
      }
      else {
        None
      }
    }
  }

  /**
   * Rollback the date to the start of the day
   */
  private def rollbackDate(cal: Calendar): Calendar = {
    cal.roll(Calendar.HOUR, 0)
    cal.roll(Calendar.MINUTE, 0)
    cal.roll(Calendar.SECOND, 0)
    cal
  }

  /**
   * The value for “Tax band” shall be calculated based on the following:
   * CO2 value 1-100 = Band A
   * 101 to 110 = Band B
   * 111 to 120 = Band C
   * 121 to 130 = Band D
   * 131 to 140 = Band E
   * 141 to 150 = Band F
   * 151 to 165 = Band G
   * 166 to 175 = Band H
   * 176 to 185 = Band I
   * 186 to 200 = Band J
   * 201 to 225 = Band K
   *
   * Where date of registration is before 23/03/2006 and CO2 value is over 225 = Band K
   *
   * Otherwise
   * 226 to 255 = Band L
   * Over 255 = Band M
   */
  def getTaxBand(firstRegistration: Date, co2Emissions: Int) = {
    if (co2Emissions >= 1 && co2Emissions <= 100) {Some("A")}
    else if (co2Emissions >= 101 && co2Emissions <= 110) {Some("B")}
    else if (co2Emissions >= 111 && co2Emissions <= 120) {Some("C")}
    else if (co2Emissions >= 121 && co2Emissions <= 130) {Some("D")}
    else if (co2Emissions >= 131 && co2Emissions <= 140) {Some("E")}
    else if (co2Emissions >= 141 && co2Emissions <= 150) {Some("F")}
    else if (co2Emissions >= 151 && co2Emissions <= 165) {Some("G")}
    else if (co2Emissions >= 166 && co2Emissions <= 175) {Some("H")}
    else if (co2Emissions >= 176 && co2Emissions <= 185) {Some("I")}
    else if (co2Emissions >= 186 && co2Emissions <= 200) {Some("J")}
    else if (co2Emissions >= 201 && co2Emissions <= 225) {Some("K")}
    else {
      // if there's no date then we can't process the rest of the logic
      if (firstRegistration == null) {None}
      else {
        var dateOfRegistrationLimit = Calendar.getInstance
        dateOfRegistrationLimit.set(2006, 02, 23)
        dateOfRegistrationLimit = rollbackDate(dateOfRegistrationLimit)

        val firstReg = Calendar.getInstance()
        firstReg.setTime(firstRegistration)

        if (firstReg.before(dateOfRegistrationLimit) && co2Emissions > 225) {
          //Where date of registration is before 23/03/2006 and CO2 value is over 225 = Band K
          Some("K")
        }
        else if (co2Emissions >= 226 && co2Emissions <= 255) {Some("L")}
        else if (co2Emissions > 255) {Some("M")}
        else {None}
      }
    }
  }

  /**
   * checks if given licensing type is SORN
   */
  def isSORN(licensingType: String) = {
    licensingType match {
      case LicensingType.SORN => true
      case _ => false
    }
  }
}

/**
 * 1 char, because it's like that in current/prev oracle db
 */
object LicensingType {
  val LICENSED = "L"
  val SORN = "S"
  val UNKNOWN = "N"
}

object TaxStatus {
  val SORN = "SORN"
  val TAXED = "Taxed"
  val EXPIRED = "Expired"
  val TAX_DUE = "Tax Due"
}
