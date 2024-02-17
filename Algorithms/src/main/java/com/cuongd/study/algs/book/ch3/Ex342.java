package com.cuongd.study.algs.book.ch3;

import static com.cuongd.study.algs.book.ch3.SeparateChainingHashST.hash;

public class Ex342<Key, Value> {
    private int m;
    private final Node<Key, Value>[] st;

    public Ex342(int m) {
        this.m = m;
        st = (Node<Key, Value>[]) new Node[m];
    }

    public Value get(Key key) {
        for (Node<Key, Value> x = st[hash(key, m)]; x != null; x = x.next) {
            if (x.key.equals(key)) {
                return x.val;
            }
        }
        return null;
    }

    public void put(Key key, Value val) {
        int keyHash = hash(key, m);
        for (Node<Key, Value> x = st[keyHash]; x != null; x = x.next) {
            if (x.key.equals(key)) {
                x.val = val;
            }
        }
        st[keyHash] = new Node<>(key, val, st[keyHash]);
    }

    private static class Node<Key, Value> {
        Key key;
        Value val;
        Node<Key, Value> next;

        Node(Key key, Value val, Node<Key, Value> next) {
            this.key = key;
            this.val = val;
            this.next = next;
        }
    }
}
