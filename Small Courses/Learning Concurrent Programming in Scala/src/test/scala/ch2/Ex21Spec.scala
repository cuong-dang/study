package com.cuongd.study.lcpis
package ch2

import ch2.Ex21.parallel

import org.scalatest.flatspec.AnyFlatSpec

import java.lang.Thread.sleep

class Ex21Spec extends AnyFlatSpec {
  "parallel" should "calculate correct results" in {
    assertResult((3, 7))(parallel(1 + 2, 3 + 4))
  }

  "parallel" should "exhibit parallel calculation behavior" in {
    val startTime = System.currentTimeMillis
    parallel({ sleep(1000); 1 }, { sleep(1000); 2 })
    val endTime = System.currentTimeMillis
    val elapsedTime = endTime - startTime
    assert(elapsedTime >= 1000 && elapsedTime < 2000)
  }
}
