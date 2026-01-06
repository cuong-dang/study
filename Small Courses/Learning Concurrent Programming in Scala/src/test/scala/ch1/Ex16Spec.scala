package com.cuongd.study.lcpis
package ch1

import ch1.Ex16.combinations

import org.scalatest.flatspec.AnyFlatSpec

class Ex16Spec extends AnyFlatSpec {
  "combinations" should "return 1-combinations of 1 element" in {
    val expected = Iterator(Seq(1))
    val actual = combinations(1, Seq(1))

    expected.zip(actual).foreach { case (exp, act) => assertResult(exp)(act) }
  }

  it should "return 1-combinations of elements" in {
    val expected = Iterator(Seq(1), Seq(2), Seq(3))
    val actual = combinations(1, Seq(1, 2, 3))

    expected.zip(actual).foreach { case (exp, act) => assertResult(exp)(act) }
  }

  it should "return 2-combinations of elements" in {
    val expected = Iterator(Seq(1, 4), Seq(1, 9), Seq(1, 16), Seq(4, 9), Seq(4, 16), Seq(9, 16))
    val actual = combinations(2, Seq(1, 4, 9, 16))

    expected.zip(actual).foreach { case (exp, act) => assertResult(exp)(act) }
  }
}
