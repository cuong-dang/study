package expressivo;

public class NumberInteger implements Expression {
    private final int n;

    public NumberInteger(int n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return String.format("%d", n);
    }
}
