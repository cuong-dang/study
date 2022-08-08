package expressivo;

public class Product extends BinaryOp implements Expression {
    public Product(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public Expression differentiate(Variable v) {
        return new Sum(
                new Product(left, right.differentiate(v)),
                new Product(right, left.differentiate(v))
        );
    }

    @Override
    public String toString() {
        return String.format("%s*%s", insertOptionalParentheses(left), insertOptionalParentheses(right));
    }

    private String insertOptionalParentheses(Expression e) {
        return e instanceof Sum ? String.format("(%s)", e) : e.toString();
    }
}
