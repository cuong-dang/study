package com.cuongd.study.fpis.ch4

import scala.annotation.tailrec

sealed trait _Option[+A] {
  def map[B](f: A => B): _Option[B] = this match {
    case _Some(a)          => _Some(f(a))
    case _None: _Option[B] => _None
  }

  def getOrElse[B >: A](default: => B): B = this match {
    case _Some(a) => a
    case _        => default
  }

  def flatMap[B](f: A => _Option[B]): _Option[B] = map(f) getOrElse _None

  def orElse[B >: A](ob: => _Option[B]): _Option[B] =
    map(_Some(_)).getOrElse(ob)

  def filter(f: A => Boolean): _Option[A] =
    flatMap(a => if (f(a)) _Some(a) else _None)
}

case class _Some[+A](get: A) extends _Option[A]
case object _None extends _Option[Nothing]

object _Option {
  def mean(xs: Seq[Double]): _Option[Double] =
    if (xs.isEmpty) _None else _Some(xs.sum / xs.length)

  def variance(xs: Seq[Double]): _Option[Double] =
    mean(xs).flatMap(m => mean(xs.map(x => math.pow(x - m, 2))))

  def lift[A, B](f: A => B): _Option[A] => _Option[B] = _.map(f)

  def map2[A, B, C](a: _Option[A], b: _Option[B])(f: (A, B) => C): _Option[C] =
    a.flatMap(aa => b.map(bb => f(aa, bb)))

  def sequence[A](as: List[_Option[A]]): _Option[List[A]] = as match {
    case Nil    => _Some(Nil)
    case h :: t => h.flatMap(a => sequence(t).map(a :: _))
  }

  def sequence2[A](as: List[_Option[A]]): _Option[List[A]] =
    as.foldRight[_Option[List[A]]](_Some(Nil))((oa, r) =>
      oa.flatMap(a => r.map(a :: _))
    )

  def sequence3[A](as: List[_Option[A]]): _Option[List[A]] =
    as.foldRight[_Option[List[A]]](_Some(Nil))((oa, r) => map2(oa, r)(_ :: _))
}
