package com.cuongd.study.leetcode

import scala.collection.mutable

object Lc0347 {
  def topKFrequent(nums: Array[Int], k: Int): Array[Int] = {
    val pq = mutable.PriorityQueue[Pair]()
    val count = mutable.Map.empty[Int, Int]
    nums.foreach(n => count.put(n, count.getOrElse(n, 0) + 1))
    count.foreach(nc => pq.addOne(Pair(nc._1, nc._2)))
    val result = mutable.ArrayBuffer.empty[Int]
    Range(0, k).foreach(_ => result.addOne(pq.dequeue().n))
    result.toArray
  }

  case class Pair(n: Int, count: Int) extends Ordered[Pair] {
    override def compare(that: Pair): Int = Integer.compare(count, that.count)
  }
}
