package com.cuongd.study.algs.book.ch3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.cuongd.study.algs.book.ch3.SeparateChainingHashST.hash;

public class Ex358<Key, Val> {
    private int n;
    private int m = 16;
    private Key[] keys;
    private Val[] vals;
    private int nWithTombs;
    private final Function<Key, Integer> hashFn;

    public Ex358() {
        keys = (Key[]) new Object[m];
        vals = (Val[]) new Object[m];
        hashFn = k -> hash(k, m);
    }

    public Ex358(int cap) {
        keys = (Key[]) new Object[cap];
        vals = (Val[]) new Object[cap];
        m = cap;
        hashFn = k -> hash(k, m);
    }

    public void put(Key key, Val val) {
        int i = hashFn.apply(key);

        if (nWithTombs >= m / 2) {
            resize(2 * m);
        }
        while (keys[i] != null) {
            i = (i + 1) % m;
        }
        keys[i] = key;
        vals[i] = val;
        n++;
        nWithTombs++;
    }

    public Val get(Key key) {
        for (int i = hashFn.apply(key); keys[i] != null; i = (i + 1) % m) {
            if (keys[i].equals(key)) {
                return vals[i];
            }
        }
        return null;
    }

    public List<Val> getAll(Key key) {
        int i = hashFn.apply(key);
        List<Val> res = new ArrayList<>();

        while (keys[i] != null && keys[i].equals(key)) {
            res.add(vals[i]);
            i = (i + 1) % m;
        }
        return res;
    }

    public boolean contains(Key key) {
        for (int i = hashFn.apply(key); keys[i] != null; i++) {
            if (keys[i].equals(key)) {
                return true;
            }
        }
        return false;
    }

    public void delete(Key key) {
        if (!contains(key)) {
            return;
        }
        int i = hashFn.apply(key);
        while (!keys[i].equals(key)) {
            i = (i + 1) % m;
        }
        keys[i] = null;
        vals[i] = null;
        n--;
        i = (i + 1) % m;
        while (keys[i] != null) {
            if (keys[i].equals(key)) {
                keys[i] = null;
                vals[i] = null;
            } else {
                Key keyToRedo = keys[i];
                Val valToRedo = vals[i];
                keys[i] = null;
                vals[i] = null;
                put(keyToRedo, valToRedo);
            }
            n--;
            i = (i + 1) % m;
        }
        if (n > 0 && n == m / 8) {
            resize(m / 2);
        }
    }

    private void resize(int cap) {
        Ex358<Key, Val> t;
        t = new Ex358<>(cap);
        for (int i = 0; i < m; i++) {
            if (keys[i] != null && vals[i] != null) {
                t.put(keys[i], vals[i]);
            }
        }
        keys = t.keys;
        vals = t.vals;
        m = t.m;
        nWithTombs = n;
    }
}
