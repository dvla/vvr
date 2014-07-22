package uk.gov.dvla.services

object ManagedService {

  val HEALTH_CHECK_ENDPOINT = "/healthcheck"

}

trait ManagedService {

  def start

  def stop

  def isAlive: Boolean

  def getName: String

}