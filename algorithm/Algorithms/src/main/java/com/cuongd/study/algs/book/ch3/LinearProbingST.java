package com.cuongd.study.algs.book.ch3;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cuongd.study.algs.book.ch3.SeparateChainingHashST.hash;

public class LinearProbingST<Key, Val> {
    private int n;
    private int m = 16;
    private Key[] keys;
    private Val[] vals;
    private boolean resizing = true;
    private int nWithTombs;
    private final Function<Key, Integer> hashFn;

    public LinearProbingST() {
        keys = (Key[]) new Object[m];
        vals = (Val[]) new Object[m];
        hashFn = k -> hash(k, m);
    }

    public LinearProbingST(int cap) {
        keys = (Key[]) new Object[cap];
        vals = (Val[]) new Object[cap];
        m = cap;
        hashFn = k -> hash(k, m);
    }

    public LinearProbingST(int cap, boolean resizing) {
        keys = (Key[]) new Object[cap];
        vals = (Val[]) new Object[cap];
        m = cap;
        this.resizing = resizing;
        hashFn = k -> hash(k, m);
    }

    public LinearProbingST(Function<Key, Integer> hashFn, int m) {
        this.m = m;
        keys = (Key[]) new Object[m];
        vals = (Val[]) new Object[m];
        this.resizing = false;
        this.hashFn = hashFn;
    }

    public void put(Key key, Val val) {
        int i;

        if (resizing && nWithTombs >= m / 2) {
            resize(2 * m);
        }
        for (i = hashFn.apply(key); keys[i] != null; i = (i + 1) % m) {
            if (keys[i].equals(key)) {
                vals[i] = val;
                return;
            }
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

    public boolean contains(Key key) {
        for (int i = hashFn.apply(key); keys[i] != null; i = (i + 1) % m) {
            if (keys[i].equals(key)) {
                return true;
            }
        }
        return false;
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
            for (int i = hashFn.apply(k); !keys[i].equals(k); i = (i + 1) % m) {
                numProbes += 1;
            }
            numProbes += 1;
        }
        return (double) numProbes / n;
    }

    public double avgMissProbes() {
        int numProbes = 0;

        for (int i = 0; i < m; i++) {
            numProbes++;
            for (int j = i; keys[j] != null; j = (j + 1) % m) {
                numProbes++;
            }
        }
        return (double) numProbes / m;
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
        i = (i + 1) % m;
        while (keys[i] != null) {
            Key keyToRedo = keys[i];
            Val valToRedo = vals[i];
            keys[i] = null;
            vals[i] = null;
            n--;
            put(keyToRedo, valToRedo);
            i = (i + 1) % m;
        }
        n--;
        if (n > 0 && n == m / 8) {
            resize(m / 2);
        }
    }

    public void delete2(Key key) {
        for (int i = 0; i < m; i++) {
            if (keys[i] != null && keys[i].equals(key)) {
                if (vals[i] != null) {
                    vals[i] = null;
                    n--;
                }
                break;
            }
        }
    }

    public Key keyAt(int index) {
        return keys[index];
    }

    public int n() {
        return n;
    }

    public int m() {
        return m;
    }

    public int nWithTombs() {
        return nWithTombs;
    }

    public Key hashesTo(Key key) {
        return keys[hashFn.apply(key)];
    }

    private void resize(int cap) {
        LinearProbingST<Key, Val> t;
        t = new LinearProbingST<>(cap);
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
