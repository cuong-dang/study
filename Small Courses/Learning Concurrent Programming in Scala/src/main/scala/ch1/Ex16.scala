package com.cuongd.study.lcpis
package ch1

object Ex16 {
  def combinations(n: Int, xs: Seq[Int]): Iterator[Seq[Int]] = {
    def iter(n: Int, xs: Seq[Int], result: Iterator[Seq[Int]]): Iterator[Seq[Int]] = {
      if (n == 0) result
      else (for ((x, i) <- xs.zipWithIndex)
        yield iter(
          n - 1,
          xs.slice(0, i) ++ xs.slice(i + 1, xs.length),
          result.map(_ ++ Seq(x)))).flatten.toIterator
    }

    iter(n, xs, Iterator(Seq()))
  }
}
