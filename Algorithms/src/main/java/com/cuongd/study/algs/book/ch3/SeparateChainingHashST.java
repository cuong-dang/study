package com.cuongd.study.algs.book.ch3;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

public class SeparateChainingHashST<Key, Value> {
    private int m;
    private SequentialSearchST<Key, Value>[] st;
    private int n;
    private int lengthTolerance;

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

    public SeparateChainingHashST(int initSize, int lengthTolerance) {
        this(initSize);
        this.n = 0;
        this.lengthTolerance = lengthTolerance;
    }

    public Value get(Key key) {
        return st[hash(key, m)].get(key);
    }

    public void put(Key key, Value val) {
        if (st[hash(key, m)].put(key, val)) {
            n++;
        }
        if (lengthTolerance != 0 && (double) n / m > lengthTolerance) {
            grow();
        }
    }

    public int n() {
        return n;
    }

    public int m() {
        return m;
    }

    public Set<Key> keys() {
        Set<Key> rv = new HashSet<>();

        for (int i = 0; i < m; i++) {
            for (Pair<Key, Value> kv : st[i].keys()) {
                rv.add(kv.getKey());
            }
        }
        return rv;
    }

    public static <Key> int hash(Key key, int m) {
        int h = key.hashCode();
        h ^= (h >>> 20) ^ (h >>> 12);
        h ^= (h >>> 7) ^ (h >>> 4);
        return h & (m - 1);
    }

    private void grow() {
        SequentialSearchST<Key, Value>[] oldSt = st;
        st = (SequentialSearchST<Key, Value>[]) new SequentialSearchST[m * 2];
        m *= 2;
        n = 0;
        for (int i = 0; i < m; ++i) {
            st[i] = new SequentialSearchST<>();
        }
        for (int i = 0; i < m / 2; i++) {
            for (Pair<Key, Value> kv : oldSt[i].keys()) {
                put(kv.getKey(), kv.getValue());
            }
        }
    }
}
