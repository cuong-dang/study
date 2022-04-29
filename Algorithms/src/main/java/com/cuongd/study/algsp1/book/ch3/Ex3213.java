package com.cuongd.study.algsp1.book.ch3;

import edu.princeton.cs.algs4.Queue;

public class Ex3213<Key extends Comparable<Key>, Value> extends BST<Key, Value> {
    @Override
    public Value get(Key key) {
        Node x = root;
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if (cmp == 0) return x.val;
            if (cmp < 0) x = x.left;
            else x = x.right;
        }
        return null;
    }

    @Override
    public void put(Key key, Value val) {
        Node x = root, px = null;
        boolean keyExisted = false;
        Queue<Node> path = new Queue<>();
        while (x != null) {
            path.enqueue(x);
            px = x;
            int cmp = key.compareTo(x.key);
            if (cmp == 0) {
                x.val = val;
                keyExisted = true;
                break;
            }
            if (cmp < 0) x = x.left;
            else x = x.right;
        }
        if (keyExisted) return;

        x = new Node(key, val, 1);
        if (px == null) root = x;
        else if (key.compareTo(px.key) < 0) px.left = x;
        else px.right = x;
        for (Node y : path) ++y.n;
    }
}
