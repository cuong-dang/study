package expressivo;

abstract class BinaryOp implements Expression {
    protected final Expression left;
    protected final Expression right;

    public BinaryOp(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
}
