package com.cuongd.study

import scala.collection.mutable

object ShardRebalancer {
  private val NumShards: Int = 10

  def rebalance(oldConf: Map[Int, Seq[Int]], servers: Seq[Int]): Map[Int, Seq[Int]] = {
    val sortedOldConf = oldConf.toSeq.sortWith((x, y) => y._2.size < x._2.size)
    val numNewServers = servers.size
    var numServersWithMoreShards = NumShards % numNewServers
    val lowerBoundNumShards = NumShards / numNewServers
    val remainingShardsToDistribute: mutable.Set[Int] = mutable.Set[Int]()
    val newConf: mutable.Map[Int, Seq[Int]] = mutable.Map[Int, Seq[Int]]()
    for (oldServerMapping <- sortedOldConf) {
      if (servers.contains(oldServerMapping._1)) {
        val numShards =
          if (numServersWithMoreShards > 0) {
            numServersWithMoreShards = numServersWithMoreShards - 1
            lowerBoundNumShards + 1
          } else lowerBoundNumShards
        val oldServer = oldServerMapping._1
        val shards = oldServerMapping._2
        newConf.put(oldServer, shards.take(numShards))
        remainingShardsToDistribute.addAll(shards.takeRight(shards.size - numShards))
      } else {
        remainingShardsToDistribute.addAll(oldServerMapping._2)
      }
    }

    val tempRoundRobin: mutable.Map[Int, mutable.Seq[Int]] = mutable.Map[Int, mutable.Seq[Int]]()
    val remainingServers = servers.filter(server => !newConf.contains(server))
    var i = 0
    for (shard <- remainingShardsToDistribute) {
      val server = remainingServers(i)
      var shards = tempRoundRobin.getOrElse(server, mutable.Seq[Int]())
      shards = shards.appended(shard)
      tempRoundRobin.put(server, shards)
      i = if (i == remainingServers.size - 1) 0 else i + 1
    }
    newConf.addAll(tempRoundRobin.map(x => (x._1, x._2.toSeq)))

    newConf.toMap
  }
}
