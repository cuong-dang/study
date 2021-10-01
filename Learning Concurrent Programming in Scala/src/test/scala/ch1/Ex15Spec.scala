package com.cuongd.study.lcpis
package ch1

import ch1.Ex15.permutations

import org.scalatest.flatspec.AnyFlatSpec

class Ex15Spec extends AnyFlatSpec {
  "permutations" should "return permutations of empty string" in {
    assertResult(Seq(""))(permutations(""))
  }

  it should "return permutations of one-length string" in {
    assertResult(Seq("a"))(permutations("a"))
  }

  it should "return permutations of two-length string" in {
    assertResult(Seq("ab", "ba"))(permutations("ab"))
  }

  it should "return permutations of longer strings" in {
    assertResult(Seq("abc", "acb", "bac", "bca", "cab", "cba"))(permutations("abc"))
  }

  it should "return permutations of longer strings with duplicates" in {
    assertResult(Seq("aac", "aca", "caa"))(permutations("aac"))
  }
}
