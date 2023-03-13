import scala.util.control.Breaks.{break, breakable}

object Lc0005FF {
  def longestPalindrome(s: String): String = {
    var ans = s(0).toString
    for (i <- 1 to s.length - 2) {
      if (ans.length < Math.min(i * 2 + 1, (s.length - 1 - i) * 2 + 1)) {
        val p = palindromeAround(s, i)
        if (p.length > ans.length) ans = p
      }
    }
    for (i <- 0 to s.length - 2) {
      val j = i + 1
      if (ans.length < Math.min((i + 1) * 2, (s.length - j) * 2)) {
        val p = palindromeAround(s, i, j)
        if (p.length > ans.length) ans = p
      }
    }
    ans
  }

  def palindromeAround(s: String, i: Int): String = {
    var j = 1
    breakable {
      while (0 <= i - j && i + j < s.length) {
        if (s(i - j) != s(i + j)) break
        j += 1
      }
    }
    s.substring(i - j + 1, i + j)
  }

  def palindromeAround(s: String, i: Int, j: Int): String = {
    var k = 0
    breakable {
      while (0 <= i - k && j + k < s.length) {
        if (s(i - k) != s(j + k)) break
        k += 1
      }
    }
    s.substring(i - k + 1, j + k)
  }

  def main(args: Array[String]): Unit = {
    println(longestPalindrome("aaaa"))
  }
}
