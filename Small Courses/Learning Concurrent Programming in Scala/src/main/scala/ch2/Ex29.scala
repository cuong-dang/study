package com.cuongd.study.lcpis
package ch2

object Ex29 {
  class PriorityTaskPoolP(val p: Int) {
    private val workers: Set[Thread] = (1 to p map {_ => mkWorker}).toSet
    private var tasks: List[(Int, () => Unit)] = List[(Int, () => Unit)]()
    private var isSorted: Boolean = false
    private val mutex: AnyRef = AnyRef

    private def mkWorker: Thread = new Thread {
      override def run(): Unit = {
        while (true) {
          var highestPriorityTask: () => Unit = null
          mutex.synchronized {
            while (tasks.isEmpty) mutex.wait()
            if (!isSorted) {
              tasks = tasks.sortBy(_._1)
              isSorted = true
            }
            highestPriorityTask = tasks.head._2
            tasks = tasks.tail
          }
          highestPriorityTask()
        }
      }
    }

    def asynchronous(priority: Int)(task: => Unit): Unit = mutex.synchronized {
      tasks = priority -> (() => task) :: tasks
      isSorted = false
      if (tasks.length == 1) mutex.notify()
    }

    def start(): Unit = workers.foreach(_.start())
  }

  def main(args: Array[String]): Unit = {
    val pq = new PriorityTaskPoolP(4)
    pq.asynchronous(1){ println("World") }
    pq.asynchronous(0){ println("Hello") }
    pq.asynchronous(4){ println("All the worker threads") }
    pq.asynchronous(3){ println("For") }
    pq.asynchronous(2){ println("Just some tasks") }
    pq.start()
    pq.asynchronous(3){ println("Goodbye") }
    pq.asynchronous(2){ println("World") }
  }
}
