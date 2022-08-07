package expressivo;

public class Variable implements Expression {
    private final String name;

    public Variable(String name) {
        this.name = name;
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
