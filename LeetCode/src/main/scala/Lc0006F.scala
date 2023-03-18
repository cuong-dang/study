object Lc0006F {
  def convert(s: String, numRows: Int): String = {
    if (numRows == 1) return s
    val numCharsInSection = 2 * (numRows - 1)
    val ans = new StringBuilder()
    var currIndex = 0
    for (i <- 0 until numRows) {
      currIndex = i
      while (currIndex < s.length) {
        ans.append(s(currIndex))
        if (i > 0 && i < numRows - 1) {
          val numCharsInBetween = numCharsInSection - (2 * i)
          val secondIndex = currIndex + numCharsInBetween
          if (secondIndex < s.length) ans.append(s(secondIndex))
        }
        currIndex += numCharsInSection
      }
    }
    ans.mkString
  }

  def main(args: Array[String]): Unit = {
    println(convert("AB", 1))
  }
}
