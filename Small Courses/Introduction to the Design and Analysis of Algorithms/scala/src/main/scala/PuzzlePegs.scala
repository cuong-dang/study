import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class Pos(x: Int, y: Int)

class PuzzlePegs(val numRows: Int, val startPos: Pos) {
  private case class Peg(pos: Pos, var isEmpty: Boolean)

  case class Move(from: Pos, to: Pos)

  private val numPegs: Int = numRows * (numRows + 1) / 2
  private var numEmptyPegs: Int = 1
  private val b: mutable.Map[Pos, Peg] = mutable.Map()

  for (i <- 0 until numRows;
       j <- 0 to i) {
    b(Pos(i, j)) = Peg(Pos(i, j), if (i == startPos.x && j == startPos.y) true else false)
  }

  private def isSolved: Boolean = numEmptyPegs == numPegs - 1

  private def inBetween(from: Pos, to: Pos): Pos = {
    if (from.x == to.x) Pos(from.x, if (to.y > from.y) from.y + 1 else from.y - 1)
    else if (from.y == to.y) Pos(if (to.x > from.x) from.x + 1 else from.x - 1, from.y)
    else Pos(if (to.x > from.x) from.x + 1 else from.x - 1, if (to.y > from.y) from.y + 1 else from.y - 1)
  }

  private def makeMove(move: Move): Unit = {
    numEmptyPegs += 1
    b(move.from).isEmpty = true
    b(inBetween(move.from, move.to)).isEmpty = true
    b(move.to).isEmpty = false
  }

  private def revertMove(move: Move): Unit = {
    b(move.from).isEmpty = false
    b(inBetween(move.from, move.to)).isEmpty = false
    b(move.to).isEmpty = true
    numEmptyPegs -= 1
  }

  private def legalMovePoses(from: Pos): Seq[Pos] = {
    val r: ListBuffer[Pos] = ListBuffer()

    if (b(from).isEmpty) {
      return List()
    }

    if (from.y - 2 >= 0 && isLegalMovePoses(from, Pos(from.x, from.y - 2)))
      r.addOne(Pos(from.x, from.y - 2)) // left
    if (from.y + 2 <= from.x && isLegalMovePoses(from, Pos(from.x, from.y + 2)))
      r.addOne(Pos(from.x, from.y + 2)) // right
    if (from.x - 2 >= 0 && from.y - 2 >= 0 && isLegalMovePoses(from, Pos(from.x - 2, from.y - 2)))
      r.addOne(Pos(from.x - 2, from.y - 2)) // up-left
    if (from.x - 2 >= 0 && from.y <= from.x - 2 && isLegalMovePoses(from, Pos(from.x - 2, from.y)))
      r.addOne(Pos(from.x - 2, from.y)) // up-right
    if (from.x + 2 < numRows && isLegalMovePoses(from, Pos(from.x + 2, from.y)))
      r.addOne(Pos(from.x + 2, from.y)) // down-left
    if (from.x + 2 < numRows && from.y + 2 <= from.x + 2 && isLegalMovePoses(from, Pos(from.x + 2, from.y + 2)))
      r.addOne(Pos(from.x + 2, from.y + 2)) // down-right
    r.toList
  }

  private def legalMoves(from: Pos): Seq[Move] = legalMovePoses(from).map(Move(from, _))

  private def isLegalMovePoses(from: Pos, to: Pos): Boolean = {
    b(to).isEmpty && !b(inBetween(from, to)).isEmpty
  }

  def solve(returnsToStarting: Boolean = false): (Boolean, List[Move]) = {
    val moves: ListBuffer[Move] = ListBuffer()
    (_solve(returnsToStarting, moves), moves.toList)
  }

  private def _solve(returnsToStarting: Boolean, moves: ListBuffer[Move]): Boolean = {
    if (isSolved) {
      if (!returnsToStarting) return true
      return moves.last.to == startPos
    }

    val allLegalMoves = b.values.flatMap(peg => legalMoves(peg.pos))
    allLegalMoves.foreach { move =>
      moves.addOne(move)
      makeMove(move)
      if (_solve(returnsToStarting, moves)) return true
      revertMove(move)
      moves.remove(moves.size - 1)
    }
    false
  }
}

object PuzzlePegs {
  def main(args: Array[String]): Unit = {
    println(new PuzzlePegs(5, Pos(0, 0)).solve(true))
  }
}
