package com.cuongd.study.algs.book.ch3;

public class SeparateChainingHashST<Key, Value> {
    private final int m;
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
        return st[hash(key, m)].get(key);
    }

    public void put(Key key, Value val) {
        st[hash(key, m)].put(key, val);
    }

    public static <Key> int hash(Key key, int m) {
        return (key.hashCode() & 0x7fffffff) % m;
    }
}
