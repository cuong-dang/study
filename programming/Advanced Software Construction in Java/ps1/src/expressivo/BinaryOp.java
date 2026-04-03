package expressivo;

import java.util.Map;
import java.util.Objects;

abstract class BinaryOp implements Expression {
    protected final Expression left;
    protected final Expression right;

    public BinaryOp(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Expression simplify(Map<String, Double> env) {
        Expression leftSimplified = left.simplify(env), rightSimplified = right.simplify(env);
        if (leftSimplified instanceof Number && rightSimplified instanceof Number) {
            return evaluateOp((Number) leftSimplified, (Number) rightSimplified);
        }
        return construct(leftSimplified, rightSimplified);
    }

    protected abstract NumberDouble evaluateOp(Number left, Number right);
    protected abstract BinaryOp construct(Expression left, Expression right);

    @Override
    public boolean equals(Object thatObject) {
        if (this == thatObject) return true;
        if (thatObject == null) return false;
        if (this.getClass() != thatObject.getClass()) return false;
        BinaryOp thatExpression = (BinaryOp) thatObject;
        return this.left.equals(thatExpression.left) && this.right.equals(thatExpression.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.left, this.right);
    }
}
