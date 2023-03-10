import scala.collection.mutable

object Lc0003FF {
  def lengthOfLongestSubstring(s: String): Int = {
    var start = 0
    var ans = 0
    val m = mutable.Map[Char, Int]()
    s.indices.foreach { i =>
      if (m.contains(s(i))) {
        start = Math.max(start, m(s(i)))
      }
      ans = Math.max(ans, i - start + 1)
      m(s(i)) = i + 1
    }
    ans
  }

  def main(args: Array[String]): Unit = {
    println(lengthOfLongestSubstring("abcabcbb"))
  }
}
