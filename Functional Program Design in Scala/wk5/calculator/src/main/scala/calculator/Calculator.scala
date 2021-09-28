package calculator

sealed abstract class Expr
final case class Literal(v: Double) extends Expr
final case class Ref(name: String) extends Expr
final case class Plus(a: Expr, b: Expr) extends Expr
final case class Minus(a: Expr, b: Expr) extends Expr
final case class Times(a: Expr, b: Expr) extends Expr
final case class Divide(a: Expr, b: Expr) extends Expr

object Calculator extends CalculatorInterface {
  def computeValues(
      namedExpressions: Map[String, Signal[Expr]]): Map[String, Signal[Double]] = {
    namedExpressions.map {
      case (ref, sigExpr) =>
        if (isCyclicRef(namedExpressions(ref)(), namedExpressions, Set(ref)))
          (ref, Signal(Double.NaN))
        else (ref, Signal(eval(sigExpr(), namedExpressions)))
    }
  }

  def eval(expr: Expr, references: Map[String, Signal[Expr]]): Double = {
    expr match {
      case Literal(v) => v
      case Plus(a, b) => eval(a, references) + eval(b, references)
      case Minus(a, b) => eval(a, references) - eval(b, references)
      case Times(a, b) => eval(a, references) * eval(b, references)
      case Divide(a, b) => eval(a, references) / eval(b, references)
      case Ref(name) => eval(getReferenceExpr(name, references), references)
    }
  }

  private def isCyclicRef(expr: Expr, references: Map[String, Signal[Expr]],
                          cyclicRefs: Set[String]): Boolean = {
    expr match {
      case Literal(_) => false
      case Plus(a, b) => isCyclicRef(a, references, cyclicRefs) ||
        isCyclicRef(b, references, cyclicRefs)
      case Minus(a, b) => isCyclicRef(a, references, cyclicRefs) ||
        isCyclicRef(b, references, cyclicRefs)
      case Times(a, b) => isCyclicRef(a, references, cyclicRefs) ||
        isCyclicRef(b, references, cyclicRefs)
      case Divide(a, b) => isCyclicRef(a, references, cyclicRefs) ||
        isCyclicRef(b, references, cyclicRefs)
      case Ref(name) =>
        if (cyclicRefs.contains(name)) true
        else isCyclicRef(getReferenceExpr(name, references), references, cyclicRefs + name)
    }
  }

  /** Get the Expr for a referenced variables.
   *  If the variable is not known, returns a literal NaN.
   */
  private def getReferenceExpr(name: String,
      references: Map[String, Signal[Expr]]) = {
    references.get(name).fold[Expr] {
      Literal(Double.NaN)
    } { exprSignal =>
      exprSignal()
    }
  }
}
