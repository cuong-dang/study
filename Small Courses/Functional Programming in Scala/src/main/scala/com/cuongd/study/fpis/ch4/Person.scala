package com.cuongd.study.fpis.ch4

case class Person(name: Name, age: Age)
sealed class Name(val value: String)
sealed class Age(val value: Int)

object Person {
  def mkName(name: String): _Either[String, Name] =
    if (name == "" || name == null) _Left("Name is empty.")
    else _Right(new Name(name))

  def mkAge(age: Int): _Either[String, Age] =
    if (age < 0) _Left("Age is out of range.")
    else _Right(new Age(age))

  def mkPerson(name: String, age: Int): _Either[List[String], Person] =
    mkName(name) match {
      case _Left(e) =>
        mkAge(age) match {
          case _Left(ee) => _Left(List(e, ee))
          case _Right(_) => _Left(List(e))
        }
      case _Right(n) =>
        mkAge(age) match {
          case _Left(ee) => _Left(List(ee))
          case _Right(a) => _Right(Person(n, a))
        }
    }
}
