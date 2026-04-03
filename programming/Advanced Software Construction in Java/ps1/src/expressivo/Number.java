package expressivo;

import java.util.Map;

abstract class Number implements Expression {
    protected final java.lang.Number n;

    public Number(java.lang.Number n) {
        this.n = n;
    }

    @Override
    public Expression differentiate(Variable v) {
        return new NumberInteger(0);
    }

    @Override
    public Expression simplify(Map<String, Double> env) {
        return this;
    }

    @Override
    public boolean equals(Object thatObj) {
        if (this == thatObj) return true;
        if (thatObj == null) return false;
        if (this.getClass() == thatObj.getClass()) return this.n.equals(((Number) thatObj).n);
        return n.doubleValue() == (((Number) thatObj).n.doubleValue());
    }

    @Override
    public int hashCode() {
        return new Double(n.doubleValue()).hashCode();
    }
}
