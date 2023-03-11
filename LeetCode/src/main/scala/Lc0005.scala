object Lc0005 {
  def longestPalindrome(s: String): String = {
    s.indices
      .map { i =>
        longestPalindromeStartingAt(s, i)
      }
      .sortWith(_.length > _.length)(0)
  }

  def longestPalindromeStartingAt(s: String, i: Int): String = {
    (i + 1 to s.length).reverse.foreach { j =>
      if (isPalindrome(s, i, j)) return s.substring(i, j)
    }
    ""
  }

  def isPalindrome(s: String, i: Int, j: Int): Boolean = {
    if (i >= j + 1) return true
    if (s(i) != s(j - 1)) false else isPalindrome(s, i + 1, j - 1)
  }

  def main(args: Array[String]): Unit = {
    println(longestPalindrome("cbbd"))
  }
}
