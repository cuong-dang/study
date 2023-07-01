package com.cuongd.study.leetcode

object Lc0010 {
  def isMatch(s: String, p: String): Boolean =
    isMatch(coalesce(parseRegEx(p)), s)

  def isMatch(ps: Seq[RegEx], s: String): Boolean = ps match {
    case Nil if s == "" => true
    case Nil            => false
    case (r @ Literal(p)) :: _ =>
      r.isMatch(s.take(p.length)) &&
      isMatch(ps.tail, s.drop(p.length))
    case (r @ Dot()) :: _ =>
      r.isMatch(s.take(1)) &&
      isMatch(ps.tail, s.tail)
    case (r @ Star(_)) :: _ =>
      (r.isMatch(s.take(1)) && isMatch(ps.tail, s.tail)) ||
      (r.isMatch(s.take(1)) && s.tail != "" && isMatch(ps, s.tail)) ||
      isMatch(ps.tail, s)
  }

  def parseRegEx(p: String): Seq[RegEx] = p match {
    case "" => Seq()
    case s if s.startsWith(".*") =>
      Seq(Star('.')) ++ parseRegEx(s.drop(2))
    case s if s.startsWith(".") =>
      Seq(Dot()) ++ parseRegEx(s.tail)
    case s if s.length > 1 && s(1) == '*' =>
      Seq(Star(s(0))) ++ parseRegEx(s.drop(2))
    case s =>
      val toDot = s.indexOf('.')
      val toStar = s.indexOf('*')
      if (toDot == -1 && toStar == -1) Seq(Literal(s))
      else if (toDot == -1 || (toStar != -1 && toStar < toDot))
        Seq(Literal(s.take(toStar - 1)), Star(s(toStar - 1))) ++
          parseRegEx(s.drop(toStar + 1))
      else Seq(Literal(s.take(toDot))) ++ parseRegEx(s.drop(toDot))
  }

  def coalesce(ps: Seq[RegEx]): Seq[RegEx] = ps match {
    case Star(c) :: Star(d) :: t if c == d =>
      Seq(Star(c)) ++ coalesce(t)
    case Star(c) :: Star(d) :: t if c == '.' || d == '.' =>
      Seq(Star('.')) ++ coalesce(t)
    case _ => ps
  }

  trait RegEx {
    def isMatch(s: String): Boolean
  }
  case class Literal(p: String) extends RegEx {
    def isMatch(s: String): Boolean = s == p
  }
  case class Dot() extends RegEx {
    def isMatch(s: String): Boolean = s.length == 1
  }
  case class Star(c: Char) extends RegEx {
    def isMatch(s: String): Boolean = if (c == '.') true
    else if (s.startsWith(c.toString)) true
    else false
  }

  def main(args: Array[String]): Unit = {
    assert(parseRegEx("a") == Seq(Literal("a")))
    assert(parseRegEx("a.") == Seq(Literal("a"), Dot()))
    assert(parseRegEx(".a") == Seq(Dot(), Literal("a")))
    assert(parseRegEx(".*") == Seq(Star('.')))
    assert(parseRegEx(".*c") == Seq(Star('.'), Literal("c")))
    assert(parseRegEx("abcd*") == Seq(Literal("abc"), Star('d')))
    assert(parseRegEx("abc.*") == Seq(Literal("abc"), Star('.')))
    assert(parseRegEx("a.bc*") == Seq(Literal("a"), Dot(), Literal("b"), Star('c')))
    assert(parseRegEx("a*b.c") == Seq(Star('a'), Literal("b"), Dot(), Literal("c")))
    assert(parseRegEx("a*b.*c") == Seq(Star('a'), Literal("b"), Star('.'), Literal("c")))
    assert(
      parseRegEx("mis*is*ip*.") == Seq(
        Literal("mi"),
        Star('s'),
        Literal("i"),
        Star('s'),
        Literal("i"),
        Star('p'),
        Dot()
      )
    )

    assert(!isMatch("aa", "a"))
    assert(isMatch("aa", "a*"))
    assert(isMatch("ab", ".*"))
    assert(isMatch("aaa", "a*a"))
    assert(isMatch("aaa", "ab*ac*a"))
    assert(isMatch("bbbba", ".*a*a"))
    assert(!isMatch("ab", ".*c"))
    assert(!isMatch("aaaaaaaaaaaaaaaaaaab", "a*a*a*a*a*a*a*a*a*a*"))
  }
}
