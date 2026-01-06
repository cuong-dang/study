import scala.collection.immutable.ArraySeq
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class AssignmentProblem(val m: IndexedSeq[IndexedSeq[Int]]) {
  private def lb(assignments: ArrayBuffer[Int]): Int = {
    val partialCost = assignments.zipWithIndex.map { case (c, i) =>
      m(i)(c)
    }.sum
    val lbRemainingCost = (assignments.size until m.size).map { p =>
      m(p).indices.filter(!assignments.contains(_)).map(m(p)(_)).min
    }.sum
    partialCost + lbRemainingCost
  }

  def solve: Array[Int] = {
    val partialAssignments = mutable.PriorityQueue[ArrayBuffer[Int]]()
    var lowestCost = Int.MaxValue
    var solution = ArrayBuffer[Int]()
    partialAssignments.addAll(m(0).indices.map(ArrayBuffer(_)))
    while (partialAssignments.nonEmpty) {
      val nextAssignment = partialAssignments.dequeue()
      val cost = lb(nextAssignment)
      if (nextAssignment.size == m.size && cost < lowestCost) {
        lowestCost = cost
        solution = nextAssignment
      } else if (
        lowestCost == Int.MaxValue || lb(nextAssignment) < lowestCost
      ) {
        partialAssignments.addAll(
          m(nextAssignment.size).indices
            .filter(!nextAssignment.contains(_))
            .map(nextAssignment ++ ArrayBuffer(_))
        )
      }
    }
    solution.toArray
  }

  implicit def ordering: Ordering[ArrayBuffer[Int]] = {
    (x: ArrayBuffer[Int], y: ArrayBuffer[Int]) => lb(y).compareTo(lb(x))
  }
}

object AssignmentProblem {
  def main(args: Array[String]): Unit = {
    val p = new AssignmentProblem(
      ArraySeq(
        ArraySeq(9, 2, 7, 8),
        ArraySeq(6, 4, 3, 7),
        ArraySeq(5, 8, 1, 8),
        ArraySeq(7, 6, 9, 4)
      )
    )
    println(p.solve.mkString("Array(", ", ", ")"))
  }
}
