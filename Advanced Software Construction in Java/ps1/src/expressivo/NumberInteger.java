package expressivo;

public class NumberInteger extends Number {
    public NumberInteger(int n) {
        super(n);
    }

    @Override
    public String toString() {
        return String.format("%d", n.intValue());
    }
}
