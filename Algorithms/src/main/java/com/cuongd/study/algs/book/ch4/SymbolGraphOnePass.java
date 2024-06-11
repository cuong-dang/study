package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;

import java.util.ArrayList;
import java.util.List;

public class SymbolGraphOnePass {
    private final ST<String, Integer> st;
    private final List<String> keys;
    private final List<Bag<String>> adj;
    private int E;

    public SymbolGraphOnePass(String stream, String sp) {
        st = new ST<>();
        keys = new ArrayList<>();
        adj = new ArrayList<>();
        In in = new In(stream);
        while (in.hasNextLine()) {
            String[] a = in.readLine().split(sp);
            String v = a[0];
            if (!st.contains(v)) {
                st.put(v, st.size());
                keys.add(v);
                adj.add(new Bag<>());
            }
            int vi = st.get(v);
            for (int i = 1; i < a.length; i++) {
                if (!st.contains(a[i])) {
                    st.put(a[i], st.size());
                    keys.add(a[i]);
                    adj.add(new Bag<>());
                }
                int wi = st.get(a[i]);
                adj.get(vi).add(a[i]);
                adj.get(wi).add(v);
                E++;
            }
        }
    }

    public int V() {
        return st.size();
    }

    public int E() {
        return E;
    }

    public void addEdge(String s, String t) {
        adj.get(st.get(s)).add(t);
        adj.get(st.get(t)).add(s);
        E++;
    }

    public Iterable<String> adj(String s) {
        return adj.get(st.get(s));
    }

    public boolean hasEdge(String s, String t) {
        for (String tt : adj.get(st.get(s))) {
            if (tt.equals(t)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(String s) {
        return st.contains(s);
    }

    public int index(String s) {
        return st.get(s);
    }

    public String name(int v) {
        return keys.get(v);
    }
}
