package expressivo;

public class Product extends BinaryOp implements Expression {
    public Product(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String toString() {
        return String.format("%s*%s", insertOptionalParentheses(left), insertOptionalParentheses(right));
    }

    private String insertOptionalParentheses(Expression e) {
        return e instanceof Sum ? String.format("(%s)", e) : e.toString();
    }
}
