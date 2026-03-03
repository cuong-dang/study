package com.cuongd.study.algs.book.ch3;

import edu.princeton.cs.algs4.Queue;

/** Threaded binary trees */
public class Ex3234<Key extends Comparable<Key>, Value> extends BST<Key, Value> {
    public Ex3234() {
        super();
    }

    @Override
    public void put(Key key, Value val) {
        root = put(root, key, val, null, null, false);
    }

    private Node put(Node x, Key key, Value val, Node parent, Node grandParent, boolean isLeftThread) {
        if (x == null) {
            Node nn = new Node(key, val, 1);
            if (isLeftThread) nn.right = parent;
            else nn.right = grandParent;
            return nn;
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = put(x.left, key, val, x, parent, true);
        else if (cmp > 0) x.right = put(getTrueRight(x), key, val, x, parent, false);
        else {
            x.val = val;
            return x;
        }
        x.n = size(x.left) + size(getTrueRight(x)) + 1;
        return x;
    }

    @Override
    public Iterable<Key> keys() {
        Queue<Key> q = new Queue<>();
        Node x = root;
        boolean threading = false;
        while (x != null) {
            if (x.left == null && !threading) {
                q.enqueue(x.key);
                if (x.right == null) {
                    break;
                }
                threading = x.n < x.right.n;
                x = x.right;
            } else if (!threading) {
                x = x.left;
            } else {
                q.enqueue(x.key);
                if (x.right == null) {
                    break;
                }
                threading = x.n < x.right.n;
                x = x.right;
            }
        }
        return q;
    }

    private Node getTrueRight(Node x) {
        return !isThread(x) ? x.right : null;
    }

    private boolean isThread(Node x) {
        if (x != null && x.right != null) {
            return x.n < x.right.n;
        }
        return false;
    }

    @Override
    protected Node delete(Node x, Key key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = delete(x.left, key);
        else if (cmp > 0) x.right = delete(getTrueRight(x), key);
        else {
            if (x.left == null) return x.right;
            if (getTrueRight(x) == null) return x.left;
            Node t = x;
            x = min(getTrueRight(t));
            x.right = deleteMin(getTrueRight(t));
            x.left = t.left;
            if (isThread(x.left) && x.left.right == t) x.left.right = x;
            else if (x.left.right != null && isThread(x.left.right) && x.left.right.right == t) x.left.right.right = x;
        }
        x.n = size(x.left) + size(getTrueRight(x)) + 1;
        return x;
    }

    @Override
    protected Node deleteMin(Node x) {
        if (x.left == null) return getTrueRight(x);
        Node t = x.left;
        x.left = deleteMin(x.left);
        if (isThread(x.left) && x.left.right == t) x.left.right = x;
        else if (x.left.right != null && isThread(x.left.right) && x.left.right.right == t) x.left.right.right = x;
        x.n = size(x.left) + size(getTrueRight(x)) + 1;
        return x;
    }

    @Override
    protected int rank(Node x, Key key) {
        if (x == null) return 0;
        if (key.compareTo(x.key) < 0) return rank(x.left, key);
        else if (key.compareTo(x.key) > 0) return 1 + rank(x.left, key) + rank(getTrueRight(x), key);
        else return size(x.left);
    }
}
