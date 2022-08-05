package expressivo;

public class NumberDouble implements Expression {
    private final double n;

    public NumberDouble(double n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return String.format("%.4f", n);
    }
}
