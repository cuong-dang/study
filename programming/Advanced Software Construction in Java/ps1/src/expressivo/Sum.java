package expressivo;

public class Sum extends BinaryOp implements Expression {
    public Sum(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    protected NumberDouble evaluateOp(Number left, Number right) {
        return new NumberDouble(left.n.doubleValue() + right.n.doubleValue());
    }

    @Override
    protected BinaryOp construct(Expression left, Expression right) {
        return new Sum(left, right);
    }

    @Override
    public Expression differentiate(Variable v) {
        return new Sum(left.differentiate(v), right.differentiate(v));
    }

    @Override
    public String toString() {
        return String.format("%s + %s", left.toString(), right.toString());
    }
}
