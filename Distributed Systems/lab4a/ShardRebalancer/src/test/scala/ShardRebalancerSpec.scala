package com.cuongd.study

import org.scalatest.flatspec.AnyFlatSpec

class ShardRebalancerSpec extends AnyFlatSpec {
  "ShardRebalancer" should "rebalance with more servers, 1 to 2, 0 out 1 in" in {
    assertResult(
      Map(
        0 -> Seq(0, 1, 2, 3, 4),
        1 -> Seq(5, 6, 7, 8, 9)
      )
    )(ShardRebalancer.rebalance(
      Map(
        0 -> Seq(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
      ),
      Seq(0, 1))
    )
  }

  it should "rebalance with more servers, 3 to 4, 1 out 2 in" in {
    assertResult(
      Map(
        0 -> Seq(0, 1, 2),
        1 -> Seq(4, 5, 6),
        3 -> Seq(3, 8),
        4 -> Seq(7, 9)
      )
    )(ShardRebalancer.rebalance(
      Map(
        0 -> Seq(0, 1, 2, 3),
        1 -> Seq(4, 5, 6),
        2 -> Seq(7, 8, 9)
      ),
      Seq(0, 1, 3, 4))
    )
  }
}
