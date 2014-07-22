package uk.gov.dvla.services.enquiry.vehicle.jdbi

import org.skife.jdbi.v2.sqlobject.{SqlQuery, Bind}
import org.skife.jdbi.v2.sqlobject.customizers.{SingleValueResult, RegisterMapper}
import uk.gov.dvla.domain.Vehicle
import java.util.Date
import uk.gov.dvla.services.enquiry.vehicle.jdbi.VehicleDao.{SELECT_COLUMNS, FROM_VEHICLE}

@RegisterMapper(Array(classOf[VehicleMapper]))
trait VehicleDao {

  @SqlQuery(SELECT_COLUMNS + FROM_VEHICLE + " and registration_number = :registration_number")
  @SingleValueResult(classOf[Vehicle])
  def findByRegistrationNumber(@Bind("registration_number") registration_number: String): Option[Vehicle]

  @SqlQuery(SELECT_COLUMNS + FROM_VEHICLE + " and fleet_number = :fleet_number order by first_registration desc OFFSET :offset LIMIT :limit")
  def findByFleet(@Bind("fleet_number") fleet_number: String, @Bind("offset") offset: Int, @Bind("limit") limit: Int): java.util.List[Vehicle]

  @SqlQuery(SELECT_COLUMNS + FROM_VEHICLE + " and fleet_number = :fleet_number order by first_registration")
  def findByFleet(@Bind("fleet_number") fleet_number: String): java.util.List[Vehicle]

  @SqlQuery(
    """
    select count(*) as vehiclesAmount
    from enquiry.vehicle
    where fleet_number = :fleet_number
  """)
  @SingleValueResult(classOf[Int])
  def countVehiclesByFleetNumber(@Bind("fleet_number") fleet_number: String): Option[Int]

  @SqlQuery("select count(*) from enquiry.vehicle where fleet_number = :fleet_number and first_registration < now() - interval '30 months'")
  @SingleValueResult(classOf[Int])
  def countOldVehiclesByFleetNumber(@Bind("fleet_number") fleet_number: String): Option[Int]

  @SqlQuery("select count(*) from enquiry.vehicle where fleet_number = :fleet_number and liability < now() + interval '60 days'")
  @SingleValueResult(classOf[Int])
  def countLiableVehiclesByFleetNumber(@Bind("fleet_number") fleet_number: String): Option[Int]

  @SqlQuery(SELECT_COLUMNS + FROM_VEHICLE +
    """
  and fleet_number = :fleet_number
  and liability <= :liable_date
  order by liability ASC
  OFFSET :offset LIMIT :limit
  """)
  def findAllWithDueLiabilityForAFleet(@Bind("fleet_number") fleet_number: String
                                       , @Bind("liable_date") liable_date: Date
                                       , @Bind("offset") offset: Int, @Bind("limit") limit: Int): java.util.List[Vehicle]

  @SqlQuery(SELECT_COLUMNS + FROM_VEHICLE +
    """
  and fleet_number = :fleet_number
  and	first_registration <= :old_age_date
  order by first_registration ASC
  OFFSET :offset LIMIT :limit
  """)
  def findAllWithOldAgeForAFleet(@Bind("fleet_number") fleet_number: String
                                 , @Bind("old_age_date") old_age_date: Date
                                 , @Bind("offset") offset: Int, @Bind("limit") limit: Int): java.util.List[Vehicle]

  @SqlQuery(SELECT_COLUMNS + FROM_VEHICLE +
    """
  and fleet_number = :fleet_number
  and	liability <= :liable_date
  order by liability ASC
  """)
  def findAllWithDueLiabilityForAFleet(@Bind("fleet_number") fleet_number: String
                                       , @Bind("liable_date") liable_date: Date): java.util.List[Vehicle]

  @SqlQuery(SELECT_COLUMNS + FROM_VEHICLE +
    """
  and fleet_number = :fleet_number
  and	first_registration <= :old_age_date
  order by first_registration ASC
  """)
  def findAllWithOldAgeForAFleet(@Bind("fleet_number") fleet_number: String
                                 , @Bind("old_age_date") old_age_date: Date): java.util.List[Vehicle]


}

object VehicleDao {

  final val SELECT_COLUMNS =
    """
    select
      registration_number,
      make,
      liability,
      first_registration,
      fleet_number,
      tc.code as tax_code,
      tc.description as tax_description,
      cylinder_capacity,
      co2_emissions,
      ftrd.value as fuel_type,
      colour,
      wp.value as wheelplan,
      bt.description as body_type,
      licensing_type,
      mass_in_service,
      number_of_seats,
      standing_capacity,
      revenue_weight,
      first_registration_uk,
      vehicle_category,
      mot_expiry,
      model,
      vin,
      last_v5,
      engine_number """

  final val FROM_VEHICLE = """
    from enquiry.vehicle v
      inner join enquiry.fuel_type ftrd on v.fuel_type = ftrd.code
      inner join enquiry.tax_class tc on v.tax_class_id = tc.id
      inner join enquiry.body_type bt on v.body_type_code = bt.code
      inner join enquiry.wheel_plan wp on v.wheelplan_code = wp.code """

}

