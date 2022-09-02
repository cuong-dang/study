package com.cuongd.study.algsp1.book.ch3;

public class SeparateChainingHashST<Key, Value> {
    protected final int m;
    private final SequentialSearchST<Key, Value>[] st;

    public SeparateChainingHashST() {
        this(997);
    }

    public SeparateChainingHashST(int m) {
        this.m = m;
        st = (SequentialSearchST<Key, Value>[]) new SequentialSearchST[m];
        for (int i = 0; i < m; ++i) {
            st[i] = new SequentialSearchST<>();
        }
    }

    public Value get(Key key) {
        return st[hash(key)].get(key);
    }

    public void put(Key key, Value val) {
        st[hash(key)].put(key, val);
    }

    protected int hash(Key key) {
        return (key.hashCode() & 0x7fffffff) % m;
    }
}
