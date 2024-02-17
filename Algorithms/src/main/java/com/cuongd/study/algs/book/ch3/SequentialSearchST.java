package com.cuongd.study.algs.book.ch3;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class SequentialSearchST<Key, Value> {
    private Node first;
    private int n;

    public Value get(Key key) {
        for (Node x = first; x != null; x = x.next) {
            if (key.equals(x.key)) {
                return x.val;
            }
        }
        return null;
    }

    public boolean put(Key key, Value val) {
        for (Node x = first; x != null; x = x.next) {
            if (key.equals(x.key)) {
                x.val = val;
                return false;
            }
        }
        first = new Node(key, val, first);
        n++;
        return true;
    }

    public List<Pair<Key, Value>> keys() {
        List<Pair<Key, Value>> rv = new ArrayList<>();
        for (Node x = first; x != null; x = x.next) {
            rv.add(new ImmutablePair<>(x.key, x.val));
        }
        return rv;
    }

    public int n() {
        return n;
    }

    public int numProbes(Key key) {
        int numProbes = 1;
        for (Node x = first; x != null; x = x.next) {
            if (x.key.equals(key)) {
                return numProbes;
            }
            numProbes++;
        }
        return numProbes;
    }

    public boolean delete(Key key) {
        for (Node x = first, px = null; x != null; x = x.next, px = x) {
            if (x.key.equals(key)) {
                if (px == null) {
                    first = x.next;
                } else {
                    px.next = x.next;
                }
                n--;
                return true;
            }
        }
        return false;
    }

    private class Node {
        Key key;
        Value val;
        Node next;

        Node(Key key, Value val, Node next) {
            this.key = key;
            this.val = val;
            this.next = next;
        }
    }
}
