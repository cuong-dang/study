import scala.collection.mutable

object ShardRebalancer {
  private val NumShards: Int = 10

  def rebalance(oldConf: Map[Int, Set[Int]], newServers: Set[Int]): Map[Int, Set[Int]] = {
    val oldConfSortedByNumShards = oldConf.toSeq.sortWith((x, y) => y._2.size < x._2.size)
    val numNewServers = newServers.size
    var numServersWithMoreShards = NumShards % numNewServers
    val lowerBoundNumShards = NumShards / numNewServers
    val remainingShardsToDistribute: mutable.Set[Int] = mutable.Set[Int]()
    val newConf: mutable.Map[Int, Set[Int]] = mutable.Map[Int, Set[Int]]()
    // First pass:
    // - Old servers that do not leave take maximum number of existing shards.
    // - Old servers that leave give up their shards for redistribution.
    for (oldServer -> shards <- oldConfSortedByNumShards) {
      if (newServers.contains(oldServer)) {
        val numShards =
          if (numServersWithMoreShards > 0) {
            numServersWithMoreShards = numServersWithMoreShards - 1
            lowerBoundNumShards + 1
          } else lowerBoundNumShards
        newConf.put(oldServer, shards.take(numShards))
        remainingShardsToDistribute.addAll(shards.takeRight(shards.size - numShards))
      } else {
        remainingShardsToDistribute.addAll(shards)
      }
    }

    // Second pass:
    // - Old servers that do not leave potentially take more shards.
    numServersWithMoreShards = NumShards % numNewServers
    val oldServersInNewConf = oldConf.keys.filter(newConf.contains)
    for (server <- oldServersInNewConf) {
       val numShards = if (numServersWithMoreShards > 0) {
         numServersWithMoreShards = numServersWithMoreShards - 1
         lowerBoundNumShards + 1
       } else lowerBoundNumShards
      if (newConf(server).size < numShards) {
        val numShardsToTake = numShards - newConf(server).size
        val shardsToTake = remainingShardsToDistribute.take(numShardsToTake)
        newConf(server) ++= shardsToTake
        remainingShardsToDistribute --= shardsToTake
      }
    }

    // Final pass:
    // - New servers take the rest of the shards.
    val remainingServers = newServers.filter(server => !newConf.contains(server))
    val t: mutable.Map[Int, mutable.Seq[Int]] = mutable.Map[Int, mutable.Seq[Int]]()
    for (newServer <- remainingServers) {
      val numShards = if (numServersWithMoreShards > 0) {
        numServersWithMoreShards = numServersWithMoreShards - 1
        lowerBoundNumShards + 1
      } else lowerBoundNumShards
      val shardsToTake = remainingShardsToDistribute.take(numShards)
      var shards = t.getOrElse(newServer, mutable.Seq[Int]())
      shards ++= shardsToTake
      t.put(newServer, shards)
      remainingShardsToDistribute --= shardsToTake
    }

    newConf.addAll(t.map(x => (x._1, x._2.toSet)))
    newConf.toMap
  }
}
