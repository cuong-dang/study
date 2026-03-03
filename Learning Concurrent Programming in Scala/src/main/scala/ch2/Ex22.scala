package com.cuongd.study.lcpis
package ch2

object Ex22 {
  def periodically(duration: Long)(b: => Unit): Unit = {
    new Thread {
      override def run(): Unit = {
        while (true) {
          b
          Thread.sleep(duration)
        }
      }
    }.start()
  }

  def main(args: Array[String]): Unit = {
    periodically(1000){ println("Hello") }
    periodically(2000){ println("World") }
  }
}
