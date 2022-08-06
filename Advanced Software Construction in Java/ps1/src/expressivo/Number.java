package expressivo;

abstract class Number {
    protected final java.lang.Number n;

    public Number(java.lang.Number n) {
        this.n = n;
    }

    @Override
    public boolean equals(Object thatObj) {
        if (this == thatObj) return true;
        if (thatObj == null) return false;
        if (this.getClass() == thatObj.getClass()) return this.n.equals(((Number) thatObj).n);
        if (this instanceof NumberInteger && thatObj instanceof NumberDouble) {
            return equals((NumberInteger) this, (NumberDouble) thatObj);
        }
        if (this instanceof NumberDouble && thatObj instanceof NumberInteger) {
            return equals((NumberInteger) thatObj, (NumberDouble) this);
        }
        return false;
    }

    protected static boolean equals(NumberInteger i, NumberDouble d) {
        return i.n.intValue() == d.n.doubleValue();
    }
}
