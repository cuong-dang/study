package com.cuongd.study.algsp1.book.ch3;

public class BinarySearchST<Key extends Comparable<Key>, Value> {
    protected final Key[] keys;
    protected final Value[] vals;
    protected int n;

    @SuppressWarnings("unchecked")
    public BinarySearchST(int cap) {
        keys = (Key[]) new Comparable[cap];
        vals = (Value[]) new Object[cap];
    }

    public Value get(Key key) {
        if (isEmpty()) return null;
        int i = rank(key);
        if (i < n && keys[i].compareTo(key) == 0) return vals[i];
        return null;
    }

    public boolean isEmpty() {
        return n == 0;
    }

    public void put(Key key, Value val) {
        int i = rank(key);
        if (i < n && keys[i].compareTo(key) == 0) {
            vals[i] = val;
            return;
        }
        for (int j = n; j > i; --j) {
            keys[j] = keys[j - 1];
            vals[j] = vals[j - 1];
        }
        keys[i] = key;
        vals[i] = val;
        ++n;
    }

    public int rank(Key key) {
        int lo = 0, hi = n - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int cmp = key.compareTo(keys[mid]);
            if (cmp < 0) hi = mid - 1;
            if (cmp > 0) lo = mid + 1;
            else return mid;
        }
        return lo;
    }
}
