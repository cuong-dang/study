package com.cuongd.study.leetcode

import scala.collection.mutable
import scala.util.control.Breaks.{break, breakable}

object Lc0006 {
  def convert(s: String, numRows: Int): String = {
    if (numRows == 1) return s
    val m = mutable.ArrayBuffer[mutable.ArrayBuffer[Char]]()
    for (_ <- 0 until numRows) { m += mutable.ArrayBuffer[Char]() }
    var row = 0
    var i = 0
    breakable {
      while (true) {
        while (row < numRows) {
          m(row) += s(i)
          row += 1
          i += 1
          if (i == s.length) break
        }
        row -= 1
        while (row > 1) {
          row -= 1
          m(row) += s(i)
          i += 1
          if (i == s.length) break
        }
        row -= 1
      }
    }
    m.flatten.mkString
  }

  def main(args: Array[String]): Unit = {
    println(convert("PAYPALISHIRING", 2))
  }
}
