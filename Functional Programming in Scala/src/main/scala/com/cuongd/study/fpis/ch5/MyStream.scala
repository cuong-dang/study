package com.cuongd.study.fpis.ch5

import com.cuongd.study.fpis.ch5.MyStream.{cons, empty, unfold}

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

sealed trait MyStream[+A] {
  def tail: MyStream[A] = this match {
    case Empty      => throw new Error("tail called on empty stream")
    case Cons(_, t) => t()
  }

  def headOption: Option[A] = this match {
    case Empty      => None
    case Cons(h, _) => Some(h())
  }

  def toListRecursive: List[A] = this match {
    case Empty      => List()
    case Cons(h, t) => h() :: t().toListRecursive
  }

  def toList: List[A] = {
    val buf: ListBuffer[A] = ListBuffer.empty

    @tailrec
    def go(s: MyStream[A]): List[A] = s match {
      case Cons(h, t) =>
        buf += h()
        go(t())
      case _ => buf.toList
    }

    go(this)
  }

  def take(n: Int): MyStream[A] = this match {
    case Cons(h, t) if n > 0 => cons(h(), t().take(n - 1))
    case _                   => empty
  }

  def drop(n: Int): MyStream[A] = this match {
    case Cons(_, t) if n > 0 => t().drop(n - 1)
    case _                   => this
  }

  def takeWhile(p: A => Boolean): MyStream[A] = this match {
    case Cons(h, t) if p(h()) => cons(h(), t().takeWhile(p))
    case _                    => empty
  }

  def foldRight[B](z: => B)(f: (A, => B) => B): B = this match {
    case Cons(h, t) => f(h(), t().foldRight(z)(f))
    case _          => z
  }

  def forAll(f: A => Boolean): Boolean =
    foldRight(true)((a, b) => f(a) && b)

  def takeWhile2(f: A => Boolean): MyStream[A] =
    foldRight(empty[A])((h, t) =>
      if (f(h)) cons(h, t)
      else empty
    )

  def headOption2: Option[A] =
    foldRight(None: Option[A])((h, _) => Some(h))

  def map[B](f: A => B): MyStream[B] =
    foldRight(empty[B])((h, t) => cons(f(h), t))

  def filter(f: A => Boolean): MyStream[A] =
    foldRight(empty[A])((h, t) =>
      if (f(h)) cons(h, t)
      else t
    )

  def append[B >: A](s: => MyStream[B]): MyStream[B] =
    foldRight(s)((h, t) => cons(h, t))

  def flatMap[B](f: A => MyStream[B]): MyStream[B] =
    foldRight(empty[B])((h, t) => f(h) append t)

  def map2[B](f: A => B): MyStream[B] = unfold(this) {
    case Cons(h, t) => Some(f(h()), t())
    case _          => None
  }

  def take2(n: Int): MyStream[A] = unfold((this, n)) {
    case (Cons(h, t), n) if n > 0 => Some(h(), (t(), n - 1))
    case _                        => None
  }

  def take3(n: Int): MyStream[A] =
    unfold((this, n)) {
      case (Cons(h, t), 1)          => Some((h(), (empty, 0)))
      case (Cons(h, t), n) if n > 1 => Some((h(), (t(), n - 1)))
      case _                        => None
    }

  def takeWhile3(p: A => Boolean): MyStream[A] = unfold(this) {
    case Cons(h, t) if p(h()) => Some(h(), t())
    case _                    => None
  }

  def zipWith[B, C](that: MyStream[B])(f: (A, B) => C): MyStream[C] =
    unfold((this, that)) {
      case (Cons(h1, t1), Cons(h2, t2)) => Some(f(h1(), h2()), (t1(), t2()))
      case _                            => None
    }

  def zipAll[B](that: MyStream[B]): MyStream[(Option[A], Option[B])] =
    unfold((this, that)) {
      case (Cons(h1, t1), Cons(h2, t2)) =>
        Some((Some(h1()), Some(h2())), (t1(), t2()))
      case (Cons(h1, t1), _) => Some((Some(h1()), None), (t1(), empty))
      case (_, Cons(h2, t2)) => Some((None, Some(h2())), (empty, t2()))
      case _                 => None
    }

  def startsWith[B >: A](s: MyStream[B]): Boolean =
    zipAll(s).takeWhile2(_._2.isDefined) forAll { case (h, h2) =>
      h == h2
    }

  def tails: MyStream[MyStream[A]] = unfold(this, tail) {
    case (Empty, Empty) => None
    case (Empty, _)     => Some(Empty, (Empty, Empty))
    case (s, Empty)     => Some(s, (Empty, s))
    case (s, t)         => Some(s, (t, t.tail))
  }

  def tails2: MyStream[MyStream[A]] = unfold(this) {
    case Empty => None
    case s     => Some(s, s drop 1)
  } append empty

  def scanRight[B](z: B)(f: (A, => B) => B): MyStream[B] = foldRight((z, MyStream(z)))((a, p0) => {
    lazy val p1 = p0
    val b2 = f(a, p1._1)
    (b2, cons(b2, p1._2))
  })._2
}

case object Empty extends MyStream[Nothing]
case class Cons[+A](h: () => A, t: () => MyStream[A]) extends MyStream[A]

object MyStream {
  def cons[A](h: => A, t: => MyStream[A]): MyStream[A] = {
    lazy val hd = h
    lazy val tl = t
    Cons(() => hd, () => tl)
  }

  def empty[A]: MyStream[A] = Empty

  def apply[A](as: A*): MyStream[A] =
    if (as.isEmpty) Empty else cons(as.head, apply(as.tail: _*))

  def constant[A](a: A): MyStream[A] = cons(a, constant(a))

  def from(n: Int): MyStream[Int] = cons(n, from(n + 1))

  def fibs: MyStream[Int] = {
    def go(a: Int, b: Int): MyStream[Int] =
      cons(a, go(b, a + b))
    go(0, 1)
  }

  def unfold[A, S](z: S)(f: S => Option[(A, S)]): MyStream[A] = f(z) match {
    case None         => empty
    case Some((a, s)) => cons(a, unfold(s)(f))
  }

  def ones: MyStream[Int] = unfold(1)(_ => Some((1, 1)))

  def constant2[A](a: A): MyStream[A] = unfold(a)(_ => Some((a, a)))

  def from2(n: Int): MyStream[Int] = unfold(n)(n => Some((n, n + 1)))

  def fibs2: MyStream[Int] = unfold((0, 1))   { case (a, b) =>
    Some(a, (b, a + b))
  }
}
