package com.prabhat

case class Country(code : Option[String])

case class Address(addressId: String , country : Option[Country])

case class Person(name : String, address : Option[Address])
