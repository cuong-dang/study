package com.cuongd.study.lcpis
package ch1

object Ex17 {
  def matcher(regex: String): PartialFunction[String, List[String]] =
    new PartialFunction[String, List[String]] {
      override def isDefinedAt(x: String): Boolean = regex.r.findAllIn(x).nonEmpty
      override def apply(v1: String): List[String] = regex.r.findAllIn(v1).toList
    }
}
