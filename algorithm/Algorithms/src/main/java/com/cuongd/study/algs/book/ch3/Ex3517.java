package com.cuongd.study.algs.book.ch3;

/**
 * MathSet
 */
public class Ex3517<Key> {
    private final Key[] universe;
    private final boolean[] keys;
    private final LinearProbingST<Key, Integer> st;
    private int n;

    public Ex3517(Key[] universe) {
        this.universe = universe;
        keys = new boolean[universe.length];
        st = new LinearProbingST<>();
        for (int i = 0; i < universe.length; i++) {
            st.put(universe[i], i);
        }
    }

    public void add(Key key) {
        if (!contains(key)) {
            n++;
        }
        keys[st.get(key)] = true;
    }

    public Ex3517<Key> complement() {
        Ex3517<Key> ans = new Ex3517<>(universe);
        for (int i = 0; i < keys.length; i++) {
            ans.keys[i] = !keys[i];
        }
        return ans;
    }

    public void union(Ex3517<Key> that) {
        for (int i = 0; i < keys.length; i++) {
            keys[i] = keys[i] || that.keys[i];
        }
    }

    public void intersection(Ex3517<Key> that) {
        for (int i = 0; i < keys.length; i++) {
            keys[i] = keys[i] && that.keys[i];
        }
    }

    public void delete(Key key) {
        if (contains(key)) {
            n--;
        }
        keys[st.get(key)] = false;
    }

    public boolean contains(Key key) {
        return keys[st.get(key)];
    }

    public boolean isEmpty() {
        return n == 0;
    }

    public int size() {
        return n;
    }
}
