package com.cuongd.study.leetcode

import scala.annotation.tailrec

object Lc0009 {
  def isPalindrome(x: Int): Boolean = {
    @tailrec
    def go(x: Int, numFrontZeroes: Int): Boolean = x match {
      case x: Int if x < 0 => false
      case x: Int if x < 10 && numFrontZeroes == 0 => true
      case x: Int if x < 10 && x > 0 && numFrontZeroes != 0 => false
      case x: Int if numFrontZeroes != 0 && x % 10 != 0 => false
      case x: Int if numFrontZeroes != 0 && x % 10 == 0 => go(x / 10, numFrontZeroes - 1)
      case x: Int =>
        def getOneZeroes(x: Int): Int = {
          @tailrec
          def go(r: Int): Int = {
            val oneZeroes = Math.pow(10, r)
            if (x / oneZeroes < 1) (oneZeroes / 10).toInt else go(r + 1)
          }

          go(1)
        }

        def getNumFrontZeroes(to: Int, from: Int): Int = {
          @tailrec
          def go(c: Int, r: Int): Int = if (c == to) r else go(c * 10, r + 1)

          if (to == 0) 0 else go(from, 0)
        }

        val oneZeroes = getOneZeroes(x)
        val next = (x % oneZeroes) / 10
        val nextOneZeroes = getOneZeroes(next)
        val numFrontZeroes = getNumFrontZeroes(oneZeroes / 100, nextOneZeroes)
        if ((x / oneZeroes) == (x % 10)) go(next, numFrontZeroes)
        else false
    }

    go(x, 0)
  }

  def main(args: Array[String]): Unit = {
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
