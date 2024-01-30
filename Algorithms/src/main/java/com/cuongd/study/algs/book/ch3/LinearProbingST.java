package com.cuongd.study.algs.book.ch3;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cuongd.study.algs.book.ch3.SeparateChainingHashST.hash;

public class LinearProbingST<Key, Val> {
    private int n;
    private int m = 16;
    private Key[] keys;
    private Val[] vals;
    private boolean resizing = true;

    public LinearProbingST() {
        keys = (Key[]) new Object[m];
        vals = (Val[]) new Object[m];
    }

    public LinearProbingST(int cap) {
        keys = (Key[]) new Object[cap];
        vals = (Val[]) new Object[cap];
        m = cap;
    }

    public LinearProbingST(int cap, boolean resizing) {
        keys = (Key[]) new Object[cap];
        vals = (Val[]) new Object[cap];
        m = cap;
        this.resizing = resizing;
    }

    public void put(Key key, Val val) {
        int i;

        if (resizing && n >= m / 2) {
            resize(2 * m);
        }
        for (i = hash(key, m); keys[i] != null; i = (i + 1) % m) {
            if (keys[i].equals(key)) {
                vals[i] = val;
                return;
            }
        }
        keys[i] = key;
        vals[i] = val;
        n++;
    }

    public Val get(Key key) {
        for (int i = hash(key, m); keys[i] != null; i = (i + 1) % m) {
            if (keys[i].equals(key)) {
                return vals[i];
            }
        }
        return null;
    }

    public Set<Key> keys() {
        return Arrays.stream(keys).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public double avgHitProbes() {
        int numProbes = 0;

        for (int j = 0; j < m; j++) {
            Key k = keys[j];
            if (k == null) {
                continue;
            }
            for (int i = hash(k, m); !keys[i].equals(k); i++) {
                numProbes += 1;
            }
            numProbes += 1;
        }
        return (double) numProbes / n;
    }

    private void resize(int cap) {
        LinearProbingST<Key, Val> t;
        t = new LinearProbingST<>(cap);
        for (int i = 0; i < m; i++) {
            if (keys[i] != null) {
                t.put(keys[i], vals[i]);
            }
        }
        keys = t.keys;
        vals = t.vals;
        m = t.m;
    }
}
