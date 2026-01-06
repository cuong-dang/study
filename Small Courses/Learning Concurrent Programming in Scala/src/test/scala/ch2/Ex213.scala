package com.cuongd.study.lcpis
package ch2

import ch2.Ex211.ConcurrentBiMap
import ch2.ThreadCommon.thread

import org.scalatest.flatspec.AnyFlatSpec

import scala.util.Random

class Ex213 extends AnyFlatSpec {
  val numTotalInserts = 100
  val numWorkingThreads = 4
  val numInsertsPerThread: Int = numTotalInserts / numWorkingThreads
  val rdn = new Random
  val hi: Int = numTotalInserts

  "ConcurrentBiMap" should "satisfy exercise 2.13's specs" in {
    /* insert */
    val cbm = new ConcurrentBiMap[Int, Int]
    val threads = 1 to numWorkingThreads map {
      _ => thread {
        var (key, value) = (0, 0)
        for (_ <- 1 to numInsertsPerThread) {
          while (key >= value) {
            key = rdn.nextInt(hi)
            value = rdn.nextInt(hi)
          }
          cbm.put(key, value)
          key = 0
          value = 0
        }
      }
    }
    threads.foreach(_.join())

    /* invert */
    val inverted = new ConcurrentBiMap[Int, Int]
    val invertThreads = 0 to numWorkingThreads+1 map {threadNo =>
      thread {
        val keyRangeStart = threadNo * numInsertsPerThread
        val keyRangeEnd = keyRangeStart + numInsertsPerThread
        for (k <- keyRangeStart until keyRangeEnd) {
          cbm.removeKey(k) match {
            case Some(v) => inverted.put(v, k)
            case None =>
          }
        }
      }
    }
    invertThreads.foreach(_.join())

    /* print */
    inverted.iterator.foreach(vk => println(s"${vk._1}, ${vk._2}"))
  }
}
