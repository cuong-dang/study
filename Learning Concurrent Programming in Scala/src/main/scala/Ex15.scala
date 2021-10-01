package com.cuongd.study.lcpis

object Ex15 {
  def permutations(x: String): Seq[String] = {
    if (x.isEmpty || x.length == 1) Seq(x)
    else (for ((c, i) <- x.zipWithIndex)
      yield permutations(x.substring(0, i) + x.substring(i+1)).map(c + _)).flatten.distinct
  }
}
