package com.cuongd.study.algsp1.book.ch3;

public class Ex342<Key, Value> extends SeparateChainingHashST<Key, Value> {
    private final Node<Key, Value>[] st;

    public Ex342(int m) {
        super(m);
        st = (Node<Key, Value>[]) new Node[m];
    }

    @Override
    public Value get(Key key) {
        for (Node<Key, Value> x = st[hash(key)]; x != null; x = x.next) {
            if (x.key.equals(key)) {
                return x.val;
            }
        }
        return null;
    }

    @Override
    public void put(Key key, Value val) {
        int keyHash = hash(key);
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
