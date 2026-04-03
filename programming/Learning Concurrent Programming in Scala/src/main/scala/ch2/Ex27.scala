package com.cuongd.study.lcpis
package ch2

import ch2.ThreadCommon.thread

import java.util.UUID.randomUUID

object Ex27 {
  class Account(val name: String, var balance: Int) {
    val uuid: String = randomUUID.toString
  }

  def sendAll(accounts: Set[Account], target: Account): Unit = {
    def loop(unSyncedAccounts: List[Account], syncedAccounts: List[Account],
             target: Account): Unit = {
      if (unSyncedAccounts.isEmpty) {
        for (src <- syncedAccounts.filter(_.uuid != target.uuid)) {
          target.balance += src.balance
          src.balance = 0
        }
      }
      else unSyncedAccounts.head.synchronized {
        loop(unSyncedAccounts.tail, unSyncedAccounts.head :: syncedAccounts, target)
      }
    }

    val sortedAccounts = (accounts.toSeq ++ Seq(target)).sortBy(_.uuid)
    loop(sortedAccounts.toList, List(), target)
  }

  def main(args: Array[String]): Unit = {
    val a = new Account("a", 100)
    val b = new Account("b", 200)
    val c = new Account("c", 300)
    val d = new Account("d", 400)
    val e = new Account("e", 500)

    val t = Set(
      thread { sendAll(Set(a, b, c, d), e) },
      thread { sendAll(Set(b, c, d, e), a) },
      thread { sendAll(Set(c, d, e, a), b) },
      thread { sendAll(Set(d, e, a, b), c) },
      thread { sendAll(Set(e, a, b, c), d) }
    )

    t.foreach(_.join())
    println(s"a = ${a.balance}; b = ${b.balance}; c = ${c.balance}; " +
      s"d = ${d.balance}; e = ${e.balance}; ")
  }
}
