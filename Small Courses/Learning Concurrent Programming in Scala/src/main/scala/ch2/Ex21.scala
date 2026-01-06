package com.cuongd.study.lcpis
package ch2

object Ex21 {
  class Executor[T](a: => T) extends Runnable {
    private var rv: T = _

    override def run(): Unit = rv = a

    def value: T = rv
  }

  def parallel[A, B](a: => A, b: => B): (A, B) = {
    val ea = new Executor[A](a)
    val eb = new Executor[B](b)
    val ta = new Thread(ea)
    val tb = new Thread(eb)

    ta.start()
    tb.start()
    ta.join()
    tb.join()
    (ea.value, eb.value)
  }
}
