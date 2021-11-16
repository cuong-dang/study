package com.cuongd.study.lcpis
package ch1

import ch1.Ex17.matcher
import org.scalatest.flatspec.AnyFlatSpec

class Ex17Spec extends AnyFlatSpec {
  "matcher" should "return correct partial function" in {
    val m = matcher(raw"(\d{4})-(\d{2})-(\d{2})")
    assertResult(false)(m.isDefinedAt("a"))
    assertResult(List("1990-01-10"))(m("1990-01-10"))
    assertResult(List("1990-01-10", "1987-08-20"))(m("1990-01-10, 1987-08-20, ..."))
  }
}
