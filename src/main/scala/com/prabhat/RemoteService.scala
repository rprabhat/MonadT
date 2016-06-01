package com.prabhat

import scala.concurrent.Future

/**
  * Created by prabhat on 1/06/2016.
  */
trait RemoteService {

  def findPerson(id : String) : Future[Option[Person]] = ???
  def findCountry(addressId : String) : Future[Option[Country]] = ???

}
