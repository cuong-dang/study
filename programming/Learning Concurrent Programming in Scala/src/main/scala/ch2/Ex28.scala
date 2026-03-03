package com.cuongd.study.lcpis
package ch2

object Ex28 {
  class PriorityTaskPool {
    private var tasks: List[(Int, () => Unit)] = List[(Int, () => Unit)]()
    private val mutex: AnyRef = AnyRef

    private val worker: Thread = new Thread {
      setDaemon(true)

      override def run(): Unit = {
        while (true) {
          mutex.synchronized {
            while (tasks.isEmpty) mutex.wait()
            tasks = tasks.sortBy(_._1)
            val highestPriorityTask = tasks.head._2
            highestPriorityTask()
            tasks = tasks.tail
          }
        }
      }
    }

    def asynchronous(priority: Int)(task: => Unit): Unit = mutex.synchronized {
      tasks = priority -> (() => task) :: tasks
      if (tasks.length == 1) mutex.notify()
    }

    def start(): Unit = worker.start()
  }

  def main(args: Array[String]): Unit = {
    val pq = new PriorityTaskPool
    pq.asynchronous(1){ println("World") }
    pq.asynchronous(0){ println("Hello") }
    pq.start()
    pq.asynchronous(3){ println("Goodbye") }
    pq.asynchronous(2){ println("World") }
  }
}
