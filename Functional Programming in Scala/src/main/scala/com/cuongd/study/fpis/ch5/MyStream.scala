package com.cuongd.study.fpis.ch5

import com.cuongd.study.fpis.ch5.MyStream.{cons, empty}

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

sealed trait MyStream[+A] {
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
    case Cons(h, t) if n > 1 => cons(h(), t().take(n - 1))
    case _ => empty
  }

  def drop(n: Int): MyStream[A] = this match {
    case Cons(_, t) if n > 0 => t().drop(n - 1)
    case _ => this
  }
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
}
