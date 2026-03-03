import org.scalatest.flatspec.AnyFlatSpec

class ShardRebalancerSpec extends AnyFlatSpec {
  "ShardRebalancer" should "rebalance with more servers, 1 to 2, 0 left" in {
    assertResult(
      Map(
        0 -> Set(0, 1, 5, 6, 9),
        1 -> Set(2, 3, 4, 7, 8)
      )
    )(ShardRebalancer.rebalance(
      Map(
        0 -> Set(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
      ),
      Set(0, 1))
    )
  }

  it should "rebalance with more servers, 1 to 2, 1 left" in {
    assertResult(
      Map(
        1 -> Set(0, 1, 2, 3, 4),
        2 -> Set(5, 6, 7, 8, 9)
      )
    )(ShardRebalancer.rebalance(
      Map(
        0 -> Set(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
      ),
      Set(1, 2))
    )
  }

  it should "rebalance with more servers, 1 to 3, 0 left" in {
    assertResult(
      Map(
        0 -> Set(0, 1, 2, 3),
        1 -> Set(4, 6, 8),
        2 -> Set(5, 7, 9)
      )
    )(ShardRebalancer.rebalance(
      Map(
        0 -> Set(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
      ),
      Set(0, 1, 2))
    )
  }

  it should "rebalance with more servers, 3 to 4, 1 left" in {
    assertResult(
      Map(
        0 -> Set(0, 1, 2),
        1 -> Set(4, 5, 6),
        3 -> Set(3, 7),
        4 -> Set(8, 9)
      )
    )(ShardRebalancer.rebalance(
      Map(
        0 -> Set(0, 1, 2, 3),
        1 -> Set(4, 5, 6),
        2 -> Set(7, 8, 9)
      ),
      Set(0, 1, 3, 4))
    )
  }

  it should "rebalance with fewer servers, 2 to 1, 1 join" in {
    assertResult(
      Map(
        2 -> Set(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
      )
    )(ShardRebalancer.rebalance(
      Map(
        0 -> Set(0, 1, 2, 3, 4),
        1 -> Set(5, 6, 7, 8, 9),
      ),
      Set(2))
    )
  }

  it should "rebalance with fewer servers, 4 to 3, 0 join" in {
    assertResult(
      Map(
        0 -> Set(0, 1, 2, 7),
        1 -> Set(4, 5, 6),
        2 -> Set(3, 8, 9),
      )
    )(ShardRebalancer.rebalance(
      Map(
        0 -> Set(0, 1, 2),
        1 -> Set(4, 5, 6),
        2 -> Set(3, 8),
        3 -> Set(7, 9)
      ),
      Set(0, 1, 2))
    )
  }

  it should "rebalance with fewer servers, 5 to 4, 1 join" in {
    assertResult(
      Map(
        0 -> Set(0, 1, 6),
        1 -> Set(2, 3, 7),
        2 -> Set(4, 5),
        5 -> Set(8, 9)
      )
    )(ShardRebalancer.rebalance(
      Map(
        0 -> Set(0, 1),
        1 -> Set(2, 3),
        2 -> Set(4, 5),
        3 -> Set(6, 7),
        4 -> Set(8, 9)
      ),
      Set(0, 1, 2, 5))
    )
  }
}
