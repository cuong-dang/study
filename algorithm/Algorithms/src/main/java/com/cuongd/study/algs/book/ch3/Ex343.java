package com.cuongd.study.algs.book.ch3;

import static com.cuongd.study.algs.book.ch3.SeparateChainingHashST.hash;

public class Ex343<Key, Value> {
    private final int m;
    private final Node<Key, Value>[] st;
    private int numKeys;

    public Ex343(int m) {
        this.m = m;
        st = (Node<Key, Value>[]) new Node[m];
        numKeys = 0;
    }

    public Value get(Key key) {
        Node<Key, Value> x = node(key);
        if (x == null) {
            return null;
        }
        return x.val;
    }

    public Integer numKeys(Key key) {
        Node<Key, Value> x = node(key);
        if (x == null) {
            return null;
        }
        return x.numKeys;
    }

    public void put(Key key, Value val) {
        int keyHash = hash(key, m);
        Node<Key, Value> x = node(key);
        if (x != null) {
            x.val = val;
        } else {
            st[keyHash] = new Node<>(key, val, numKeys, st[keyHash]);
            ++numKeys;
        }
    }

    public void deleteGtNumKeys(int numKeys) {
        for (int i = 0; i < m; ++i) {
            for (Node<Key, Value> curr = st[i], prev = null; curr != null; prev = curr, curr = curr.next) {
                /* Delete heads */
                while (st[i] != null && st[i].numKeys > numKeys) {
                    st[i] = st[i].next;
                    curr = st[i];
                }
                if (curr != null && curr.numKeys > numKeys) {
                    prev.next = curr.next;
                    curr = prev;
                }
            }
        }
    }

    private Node<Key, Value> node(Key key) {
        for (Node<Key, Value> x = st[hash(key, m)]; x != null; x = x.next) {
            if (x.key.equals(key)) {
                return x;
            }
        }
        return null;
    }

    private static class Node<Key, Value> {
        Key key;
        Value val;
        int numKeys;
        Node<Key, Value> next;

        Node(Key key, Value val, int numKeys, Node<Key, Value> next) {
            this.key = key;
            this.val = val;
            this.numKeys = numKeys;
            this.next = next;
        }
    }
}
