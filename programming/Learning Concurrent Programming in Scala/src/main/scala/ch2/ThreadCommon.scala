package com.cuongd.study.lcpis
package ch2

object ThreadCommon {
  def thread(body: => Unit): Thread = {
    val t = new Thread {
      override def run(): Unit = body
    }

    t.start()
    t
  }
}
