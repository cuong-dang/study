object Lc0005F {
  def longestPalindrome(s: String): String = {
    val m = Array.ofDim[Boolean](s.length, s.length)
    s.indices.foreach { i => m(i)(i) = true }
    2 to s.length foreach { len =>
      0 to s.length - len foreach { i =>
        if (len == 2) m(i)(i + 1) = s(i) == s(i + 1)
        else if (len == 3) m(i)(i + 2) = s(i) == s(i + 2)
        else m(i)(i + len - 1) = s(i) == s(i + len - 1) && m(i + 1)(i + len - 2)
      }
    }
    (1 to s.length).reverse.foreach { len =>
      0 to s.length - len foreach { i =>
        if (m(i)(i + len - 1)) return s.substring(i, i + len)
      }
    }
    ""
  }

  def main(args: Array[String]): Unit = {
    println(longestPalindrome("cbbd"))
  }
}
