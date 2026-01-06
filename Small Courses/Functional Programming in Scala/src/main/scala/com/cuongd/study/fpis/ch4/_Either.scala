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
      case _Left(e)  => _Left(e)
      case _Right(a) => f(a)
    }

  def map2[EE >: E, B, C](b: _Either[EE, B])(f: (A, B) => C): _Either[EE, C] =
    for { a <- this; b <- b } yield f(a, b)
}

case class _Left[+E](error: E) extends _Either[E, Nothing]
case class _Right[+A](value: A) extends _Either[Nothing, A]

object _Either {
  def _Try[A](a: => A): _Either[Exception, A] =
    try _Right(a)
    catch { case e: Exception => _Left(e) }

  def sequence[E, A](as: List[_Either[E, A]]): _Either[E, List[A]] =
    as.foldRight[_Either[E, List[A]]](_Right(Nil))((a, r) => a.map2(r)(_ :: _))

  def traverse[E, A, B](
      as: List[_Either[E, A]]
  )(f: A => B): _Either[E, List[B]] =
    as.foldRight[_Either[E, List[B]]](_Right(Nil))((a, r) =>
      a.map2(r)(f(_) :: _)
    )
}
