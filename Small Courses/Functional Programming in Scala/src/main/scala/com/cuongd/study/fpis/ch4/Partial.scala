package com.cuongd.study.fpis.ch4

sealed trait Partial[+E, +A] {
  def map[B](f: A => B): Partial[E, B] = this match {
    case Errors(get)  => Errors(get)
    case Success(get) => Success(f(get))
  }

  def orElse[EE >: E, B >: A](b: => Partial[EE, B]): Partial[EE, B] =
    this match {
      case Errors(_)    => b
      case Success(get) => Success(get)
    }

  def flatMap[EE >: E, B >: A](f: A => Partial[EE, B]): Partial[EE, B] =
    this match {
      case Errors(get)  => Errors(get)
      case Success(get) => f(get)
    }

  def map2[EE >: E, B, C](b: Partial[EE, B])(f: (A, B) => C): Partial[EE, C] =
    (this, b) match {
      case (Errors(aes), Errors(bes)) => Errors(aes ++ bes)
      case (Errors(aes), _)           => Errors(aes)
      case (_, Errors(bes))           => Errors(bes)
      case (Success(a), Success(b))   => Success(f(a, b))
    }
}

case class Errors[+E](get: Seq[E]) extends Partial[E, Nothing]
case class Success[+A](get: A) extends Partial[Nothing, A]

object Partial {
  def sequence[E, A](ps: List[Partial[E, A]]): Partial[E, List[A]] =
    traverse(ps)(a => a)

  def traverse[E, A, B](
      as: List[Partial[E, A]]
  )(f: A => B): Partial[E, List[B]] =
    as.foldRight[Partial[E, List[B]]](Success(List()))((p, r) =>
      p.map2(r)(f(_) :: _)
    )
}
