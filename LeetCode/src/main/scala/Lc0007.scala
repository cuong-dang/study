import scala.annotation.tailrec

object Lc0007 {
  def reverse(x: Int): Int = {
    val reversed = x.toString.reverse
    val ansStr = if (reversed.last == '-') s"-${reversed.dropRight(1)}" else reversed
    if (
      intStrLt(ansStr, Int.MinValue.toString, false) ||
      intStrLt(Int.MaxValue.toString, ansStr, false)
    ) 0
    else ansStr.toInt
  }

  def intStrLt(a: String, b: String, isNeg: Boolean): Boolean = {
    if (a.head == '0' && a.length > 1) intStrLt(a.tail, b, isNeg)
    else if (b.head == '0' && b.length > 1) intStrLt(a, b.tail, isNeg)
    else if (a.head == '-' && b.head == '-') intStrLt(a.tail, b.tail, true)
    else if (a.head == '-') true
    else if (b.head == '-') false
    else if (a.length < b.length && !isNeg) true
    else if (a.length < b.length && isNeg) false
    else if (a.length > b.length && !isNeg) false
    else if (a.length > b.length && isNeg) false
    else if (a.head.toInt < b.head.toInt && !isNeg) true
    else if (a.head.toInt < b.head.toInt && isNeg) false
    else if (a.head.toInt > b.head.toInt && !isNeg) false
    else if (a.head.toInt > b.head.toInt && isNeg) true
    else intStrLt(a.tail, b.tail, isNeg)
  }

  def main(args: Array[String]): Unit = {
    assert(reverse(0) == 0)
  }
}
