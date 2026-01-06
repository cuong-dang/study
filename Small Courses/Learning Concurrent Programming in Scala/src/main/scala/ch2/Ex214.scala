package com.cuongd.study.lcpis
package ch2

import scala.collection.mutable

object Ex214 {
  def cache[K, V](f: K => V): K => V = {
    val mem = mutable.Map[K, V]()

    def doIt(k: K): V = {
      val cached = mem.synchronized { mem.get(k) }

      if (cached.isDefined) return cached.get

      val newVal = f(k)
      mem.synchronized { mem.put(k, newVal) }
      newVal
    }
    doIt
  }
}
