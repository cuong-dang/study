package com.cuongd.study.fpis.ch4

sealed trait _Either[+E, +A] {
  def map[B](f: A => B): _Either[E, B] = this match {
    case _Left(e)  => _Left(e)
    case _Right(a) => _Right(f(a))
  }

  def orElse[EE >: E, B >: A](b: => _Either[EE, B]): _Either[EE, B] =
    this match {
      case _Left(_)  => b
      case _Right(a) => _Right(a)
    }

  def flatMap[EE >: E, B](f: A => _Either[EE, B]): _Either[EE, B] =
    this match {
      case _Left(e) => _Left(e)
      case _Right(a) => f(a)
    }

  def map2[EE >: E, B, C](b: _Either[EE, B])(f: (A, B) => C): _Either[EE, C] =
    for { a <- this; b <- b } yield f(a, b)
}

case class _Left[+E](error: E) extends _Either[E, Nothing]
case class _Right[+A](value: A) extends _Either[Nothing, A]
