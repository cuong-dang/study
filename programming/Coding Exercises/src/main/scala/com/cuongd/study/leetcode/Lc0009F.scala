package com.cuongd.study.leetcode

import scala.annotation.tailrec

object Lc0009F {
  def isPalindrome(x: Int): Boolean = {
    if (x < 0) return false

    var h = length(x) - 1
    var l = 0
    while (l < h) {
      if (kth_digit(x, l) != kth_digit(x, h)) return false
      l += 1
      h -= 1
    }
    true
  }

  def length(x: Int): Int = {
    @tailrec
    def go(x: Int, acc: Int): Int = if (x < 10) acc else go(x / 10, acc + 1)

    go(x, 1)
  }

  def kth_digit(x: Int, i: Int): Int = x / Math.pow(10, i).toInt % 10

  def main(args: Array[String]): Unit = {
    assert(length(1) == 1)
    assert(length(3) == 1)
    assert(length(17) == 2)
    assert(length(100) == 3)
    assert(isPalindrome(121))
    assert(!isPalindrome(-121))
    assert(!isPalindrome(10))
    assert(isPalindrome(11))
    assert(!isPalindrome(1000021))
    assert(isPalindrome(100020001))
    assert(isPalindrome(1001))
    assert(!isPalindrome(1234567899))
  }
}
