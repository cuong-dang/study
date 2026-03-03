package com.cuongd.study.lcpis
package ch2

object Ex23 {
  class SyncVar[T] {
    var value: Option[T] = None

    def isEmpty: Boolean = value.synchronized {
      value.isEmpty
    }

    def nonEmpty: Boolean = value.synchronized {
      value.isDefined
    }

    def get(): T = value.synchronized {
      val r = value.get
      value = None
      r
    }

    def put(x: T): Unit = value.synchronized {
      value = Some(x)
    }
  }
}
