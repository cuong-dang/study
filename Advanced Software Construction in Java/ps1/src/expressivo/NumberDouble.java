package expressivo;

public class NumberDouble extends Number implements Expression {
    public NumberDouble(double n) {
        super(n);
    }

    @Override
    public String toString() {
        return String.format("%.4f", n.doubleValue());
    }
}
