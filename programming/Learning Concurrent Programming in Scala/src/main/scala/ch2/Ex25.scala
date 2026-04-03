package com.cuongd.study.lcpis
package ch2

import ch2.ThreadCommon.thread

object Ex25 {
  class SyncVar[T] {
    private var value: Option[T] = None
    private val getLock: AnyRef = new AnyRef
    private val putLock: AnyRef = new AnyRef

    def get(): T = {
      getLock.synchronized { while (value.isEmpty) getLock.wait() }
      value.synchronized {
        val v = value.get
        value = None
        putLock.synchronized { putLock.notify() }
        v
      }
    }

    def put(x: T): Unit = {
      putLock.synchronized { while (value.isDefined) putLock.wait() }
      value.synchronized { value = Some(x) }
      getLock.synchronized { getLock.notify() }
    }
  }

  def main(args: Array[String]): Unit = {
    val sync = new SyncVar[Int]
    thread { for (i <- 0 to 9) sync.put(i) }
    thread { 0 to 9 foreach { _ => println(sync.get()) } }
  }
}
