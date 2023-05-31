package com.cuongd.study.fpis.ch4

sealed trait _Option[+A] {
  def map[B](f: A => B): _Option[B] = this match {
    case _Some(a) => _Some(f(a))
    case _None: _Option[B] => _None
  }

  def getOrElse[B >: A](default: => B): B = this match {
    case _Some(a) => a
    case _ => default
  }

  def flatMap[B](f: A => _Option[B]): _Option[B] = map(f) getOrElse _None

  def orElse[B >: A](ob: => _Option[B]): _Option[B] =
    map(_Some(_)).getOrElse(ob)

  def filter(f: A => Boolean): _Option[A] =
    flatMap(a => if (f(a)) _Some(a) else _None)
}

case class _Some[+A](get: A) extends _Option[A]
case object _None extends _Option[Nothing]
