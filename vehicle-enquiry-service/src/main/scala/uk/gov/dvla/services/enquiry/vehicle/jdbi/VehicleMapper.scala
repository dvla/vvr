package uk.gov.dvla.services.enquiry.vehicle.jdbi

import org.skife.jdbi.v2.tweak.ResultSetMapper
import uk.gov.dvla.domain.Vehicle
import java.sql.ResultSet
import org.skife.jdbi.v2.StatementContext

class VehicleMapper extends ResultSetMapper[Vehicle] {

  implicit class ResultSetWithOptions(r:ResultSet) {

    def getIntOpt(columnLabel:String) = {
      val v = r.getInt(columnLabel)
      if (r.wasNull) None else Some(v)
    }

    def getDateOpt(columnLabel:String) = {
      val v = r.getDate(columnLabel)
      if (r.wasNull) None else Some(v)
    }

    def getStringOpt(columnLabel:String) = {
      val v = r.getString(columnLabel)
      if (r.wasNull) None else Some(v)
    }

  }

  def map(index:Int, r:ResultSet, ctx:StatementContext) = {

    new Vehicle(
      r.getString("registration_number"),
      r.getString("make"),
      r.getDate("first_registration"),
      r.getString("fleet_number"),
      r.getString("tax_code"),
      r.getString("tax_description"),
      r.getString("fuel_type"),
      r.getString("colour"),
      r.getString("wheelplan"),
      r.getString("body_type"),
      r.getString("licensing_type"),
      r.getDateOpt("liability"),
      r.getIntOpt("cylinder_capacity"),
      r.getIntOpt("co2_emissions"),
      r.getIntOpt("mass_in_service"),
      r.getIntOpt("number_of_seats"),
      r.getIntOpt("standing_capacity"),
      r.getIntOpt("revenue_weight"),
      r.getDateOpt("first_registration_uk"),
      r.getStringOpt("vehicle_category"),
      r.getDateOpt("mot_expiry"),
      r.getStringOpt("model"),
      r.getStringOpt("vin"),
      r.getDateOpt("last_v5"),
      r.getStringOpt("engine_number")
    )

  }

}
