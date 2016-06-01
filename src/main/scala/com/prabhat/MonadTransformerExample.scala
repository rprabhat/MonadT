package com.prabhat


import scala.concurrent.Future
import scalaz.OptionT
import scalaz._
import Scalaz._


object MonadTransformerExample extends RemoteService {

  def getCountryCode_1(maybePerson: Option[Person]): Option[String] = {
    maybePerson flatMap { person =>
      person.address flatMap { address =>
        address.country flatMap { country =>
          country.code
        }
      }
    }
  }

  def getCountryCode_2(maybePerson: Option[Person]): Option[String] = for {
    person <- maybePerson
    address <- person.address
    country <- address.country
    code <- country.code
  } yield code


  def getCountryCode_3(personId: String) = {
    findPerson(personId) map { maybePerson =>
      maybePerson map { person =>
        person.address map { address =>
          findCountry(address.addressId) map { maybeCountry =>
            maybeCountry map { country =>
              country.code
            }
          }
        }
      }
    }
  }

  def getCountryCode_4(personId: String): Future[Option[String]] = {
    findPerson(personId) flatMap { case Some(Person(_, Some(address))) =>
      findCountry(address.addressId) map { case Some(Country(code)) =>
        code
      }
    }
  }

  def getCountryCode_5(personId : String): Future[Option[String]] = {
    for {
      maybePerson <- findPerson(personId)
      person <- Future.successful {
        maybePerson getOrElse (throw new NoSuchElementException("..."))
      }
      address <- Future.successful {
        person.address getOrElse (throw new NoSuchElementException("..."))
      }
      maybeCountry <- findCountry(address.addressId)
      country <- Future.successful {
        maybeCountry getOrElse (throw new NoSuchElementException("..."))
      }
    } yield country.code
  }

  def getCountryCode_5(personId : String): Future[Option[String]] = {

    val result : OptionT[Future, String] = for {
      person <- OptionT(findPerson(personId))
      address <- OptionT(Future.successful(person.address))
      country <- OptionT(findCountry(address.addressId))
      code <- OptionT(Future.successful(country.code))
    } yield code

    result.run
  }


  type Result[A] = OptionT[Future, A]

  object ? {
    def <~[A](v: Future[Option[A]]): Result[A] = OptionT(v)
    def <~[A](v: Option[A]): Result[A] = OptionT(Future.successful(v))
    def <~[A](v: A): Result[A] = v.point[Result]
  }

  def getCountryCode_6(personId: String): Future[Option[String]] = {

    val result: Result[String] = for {
      person  <- ? <~ findPerson(personId)
      address <- ? <~ person.address
      country <- ? <~ findCountry(address.addressId)
      code    <- ? <~ country.code
    } yield code

    result.run
  }



  def main(args: Array[String]): Unit = {

    type Result[A] = OptionT[Future, A]

    val result: Result[String] = "".point[Result]

    val future: Future[Option[String]] = "".point[Result].run
  }

}
