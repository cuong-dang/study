object Lc0008 {
  def myAtoi(s: String): Int = {
    def iter(s: String, ans: Double, isNeg: Boolean, firstChar: Boolean): Double = {
      if (s.isEmpty) if (isNeg) -ans else ans
      else {
        val c = s.charAt(0)
        if (firstChar && c == '-') iter(s.substring(1), 0, true, false)
        else if (firstChar && c == '+') iter(s.substring(1), 0, false, false)
        else if (!Character.isDigit(c)) iter("", ans, isNeg, false)
        else iter(s.substring(1), 10 * ans + c - '0', isNeg, false)
      }
    }

    if (s.isEmpty) 0
    else if (s.charAt(0) == ' ') myAtoi(s.substring(1))
    else {
      val ans = iter(s, 0, false, true)
      if (ans < Int.MinValue) Int.MinValue
      else if (ans > Int.MaxValue) Int.MaxValue
      else ans.toInt
    }
  }

  def main(args: Array[String]): Unit = {
    assert(myAtoi("42") == 42)
    assert(myAtoi("   -42") == -42)
    assert(myAtoi("   42 ") == 42)
  }
}
