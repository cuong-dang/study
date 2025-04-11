package com.cuongd.study.leetcode

object Lc0034 {
  def searchRange(nums: Array[Int], target: Int): Array[Int] = {
    if (nums.isEmpty) return Array(-1, -1)
    searchRange(nums, target, 0, nums.length - 1)
  }

  def searchRange(a: Array[Int], target: Int, lo: Int, hi: Int): Array[Int] = {
    if (lo == hi) {
      if (a(lo) == target) return Array(lo, hi)
      return Array(-1, -1)
    }
    val mid = (lo + hi) / 2
    val left = searchRange(a, target, lo, mid)
    val right = searchRange(a, target, mid + 1, hi)
    Array(
      if (
        (left.head < right.head && left.head != -1)
        || right.head == -1
      ) left.head
      else right.head,
      if (left.last > right.last) left.last else right.last
    )
  }
}
