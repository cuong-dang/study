package com.cuongd.study.leetcode.lc0010

import scala.collection.mutable

object Lc0003F {
  def lengthOfLongestSubstring(s: String): Int = {
    if (s.isEmpty) return 0
    var answer = 0
    var indexMap = mutable.Map[Char, Int]()
    var currLength = 0
    s.indices.foreach { i =>
      val c = s(i)
      if (!indexMap.contains(c)) {
        indexMap(c) = i
        currLength += 1
      } else {
        if (currLength > answer) answer = currLength
        val seenIndex = indexMap(c)
        currLength = i - seenIndex
        indexMap = indexMap.filter(ci => ci._2 > seenIndex)
        indexMap(c) = i
      }
    }
    if (currLength > answer) answer = currLength
    answer
  }

  def main(args: Array[String]): Unit = {
    println(lengthOfLongestSubstring("abcabcbb"))
  }
}
