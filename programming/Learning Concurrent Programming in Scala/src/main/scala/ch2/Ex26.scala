package com.cuongd.study.lcpis
package ch2

import ch2.ThreadCommon.thread

import scala.collection.mutable

object Ex26 {
  class SyncQueue[A](private val c: Int) {
    private val q: mutable.Queue[A] = mutable.Queue[A]()
    private var n: Int = 0
    private val putLock: AnyRef = new AnyRef
    private val getLock: AnyRef = new AnyRef

    def put(item: A): Unit = {
      while (n.synchronized { n == c }) putLock.synchronized { putLock.wait() }
      q.synchronized {
        q.enqueue(item)
        n.synchronized {
          n += 1
          if (n == 1) {
            getLock.synchronized {
              getLock.notify()
            }
          }
        }
      }
    }

    def get(): A = {
      while (n.synchronized { n == 0 }) getLock.synchronized { getLock.wait() }
      q.synchronized {
        val item = q.dequeue
        n.synchronized {
          n -= 1
          if (n == c - 1) {
            putLock.synchronized {
              putLock.notify()
            }
          }
        }
        item
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val q = new SyncQueue[Int](4)
    thread { for (i <- 0 to 9) q.put(i) }
    thread { for (_ <- 0 to 9) println(q.get()) }
  }
}
