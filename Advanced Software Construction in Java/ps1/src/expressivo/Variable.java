package expressivo;

import java.util.Map;

public class Variable implements Expression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public Expression differentiate(Variable v) {
        if (this.equals(v)) {
            return new NumberInteger(1);
        }
        return new NumberInteger(0);
    }

    @Override
    public Expression simplify(Map<String, Double> env) {
        Double value = env.get(name);
        if (value == null) {
            return this;
        } else {
            return new NumberDouble(value);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object thatObj) {
        if (this == thatObj) return true;
        if (thatObj == null) return false;
        if (this.getClass() == thatObj.getClass()) return this.name.equals(((Variable) thatObj).name);
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
