package com.cuongd.study.lcpis
package ch2

import ch2.Ex23.SyncVar
import ch2.ThreadCommon.thread

object Ex24 extends App {
  val syncVar = new SyncVar[Int]

  val producer = thread {
    for (i <- 0 until 15) {
      var next = false

      while (!next) {
        if (syncVar.isEmpty) {
          syncVar.put(i)
          next = true
        }
      }
    }
  }

  val consumer = thread {
    var done = false

    while (!done)
      if (syncVar.nonEmpty) {
        val got = syncVar.get()
        if (got == 14) done = true
        println(got)
      }
  }
}
