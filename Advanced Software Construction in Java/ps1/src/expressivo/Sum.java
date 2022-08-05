package expressivo;

public class Sum extends BinaryOp implements Expression {
    public Sum(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String toString() {
        return String.format("%s + %s", left.toString(), right.toString());
    }
}
