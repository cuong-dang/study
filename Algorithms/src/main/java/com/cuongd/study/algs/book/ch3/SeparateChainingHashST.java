package com.cuongd.study.algs.book.ch3;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

import static edu.princeton.cs.algs4.StdOut.println;

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

    public void put2(Key key, Value val) {
        int h1 = 11 * key.hashCode() % m;
        int h2 = 17 * key.hashCode() % m;

        if (st[h1].n() < st[h2].n()) {
            if (st[h1].put(key, val)) {
                n++;
            }
        } else {
            if (st[h2].put(key, val)) {
                n++;
            }
        }
    }

    public double avgHitProbes2() {
        int numProbes = 0;

        for (Key key : keys()) {
            int h1 = 11 * key.hashCode() % m;
            int h2 = 17 * key.hashCode() % m;

            if (st[h1].get(key) != null) {
                numProbes += st[h1].numProbes(key);
            } else {
                st[h2].numProbes(key);
            }
        }
        return (double) numProbes / n;
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

    public static void main(String[] args) {
        SeparateChainingHashST<Character, Integer> st = new SeparateChainingHashST<>(3);

        st.put2('E', 0);
        st.put2('A', 0);
        st.put2('S', 0);
        st.put2('Y', 0);
        st.put2('Q', 0);
        st.put2('U', 0);
        st.put2('T', 0);
        st.put2('I', 0);
        st.put2('O', 0);
        st.put2('N', 0);
        println(st.avgHitProbes2());
    }
}
