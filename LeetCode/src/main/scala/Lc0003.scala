import scala.collection.mutable
import scala.util.control.Breaks.{break, breakable}

object Lc0003 {
  def lengthOfLongestSubstring(s: String): Int = {
    if (s.isEmpty) return 0
    val candidates = mutable.ListBuffer[String]()
    s.indices.foreach { i =>
      val seen = mutable.Set[Char](s(i))
      var j = i + 1
      breakable {
        while (j < s.length) {
          if (seen.contains(s(j))) break
          seen.add(s(j))
          j += 1
        }
      }
      candidates.addOne(s.substring(i, j))
    }
    candidates.map(_.length).max
  }

  def main(args: Array[String]): Unit = {
    println(lengthOfLongestSubstring("abcabcbb"))
  }
}
