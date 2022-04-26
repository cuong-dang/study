package com.cuongd.study.algsp1.book.ch3;

import edu.princeton.cs.algs4.Queue;

import java.util.NoSuchElementException;

public class BST<Key extends Comparable<Key>, Value> implements OrderedST<Key, Value> {
    private Node root;

    @Override
    public void put(Key key, Value val) {
        root = put(root, key, val);
    }

    private Node put(Node x, Key key, Value val) {
        if (x == null) return new Node(key, val, 1);
        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = put(x.left, key, val);
        else if (cmp > 0) x.right = put(x.right, key, val);
        else x.val = val;
        x.n = size(x.left) + size(x.right) + 1;
        return x;
    }

    @Override
    public Value get(Key key) {
        return get(root, key);
    }

    private Value get(Node x, Key key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) return get(x.left, key);
        if (cmp > 0) return get(x.right, key);
        return x.val;
    }

    @Override
    public void delete(Key key) {
        root = delete(root, key);
    }

    private Node delete(Node x, Key key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = delete(x.left, key);
        else if (cmp > 0) x.right = delete (x.right, key);
        else {
            if (x.left == null) return x.right;
            if (x.right == null) return x.left;
            Node t = x;
            x = min(t.right);
            x.right = deleteMin(t.right);
            x.left = t.left;
        }
        x.n = size(x.left) + size(x.right) + 1;
        return x;
    }

    @Override
    public boolean contains(Key key) {
        return get(key) != null;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(Node x) {
        if (x == null) return 0;
        return x.n;
    }

    @Override
    public Key min() {
        Node x = min(root);
        if (x == null) return null;
        return x.key;
    }

    private Node min(Node x) {
        if (x == null) return null;
        if (x.left == null) return x;
        return min(x.left);
    }

    @Override
    public Key max() {
        Node x = max(root);
        if (x == null) return null;
        return x.key;
    }

    private Node max(Node x) {
        if (x == null) return null;
        if (x.right == null) return x;
        return min(x.right);

    }

    @Override
    public Key floor(Key key) {
        Node x = floor(root, key);
        if (x == null) return null;
        return x.key;
    }

    private Node floor(Node x, Key key) {
        if (x == null) return null;
        if (key.compareTo(x.key) == 0) return x;
        if (key.compareTo(x.key) < 0) return floor(x.left, key);
        Node t = floor(x.right, key);
        if (t == null) return x;
        return t;
    }

    @Override
    public Key ceiling(Key key) {
        Node x = ceiling(root, key);
        if (x == null) return null;
        return x.key;
    }

    private Node ceiling(Node x, Key key) {
        if (x == null) return null;
        if (key.compareTo(x.key) == 0) return x;
        if (key.compareTo(x.key) > 0) return ceiling(x.right, key);
        Node t = ceiling(x.left, key);
        if (t == null) return x;
        return t;
    }

    @Override
    public int rank(Key key) {
        return rank(root, key);
    }

    private int rank(Node x, Key key) {
        if (x == null) return 0;
        if (key.compareTo(x.key) < 0) return rank(x.left, key);
        else if (key.compareTo(x.key) > 0) return 1 + rank(x.left, key) + rank(x.right, key);
        else return size(x.left);
    }

    @Override
    public Key select(int k) {
        if (isEmpty()) throw new NoSuchElementException();
        return select(root, size(root.left), k).key;
    }

    private Node select(Node x, int rank, int k) {
        if (rank == k) return x;
        if (rank > k && x.left != null) return select(x.left, size(x.left.left), k);
        else if (x.right != null) return select(x.right, 1 + size(x.left), k);
        throw new NoSuchElementException();
    }

    @Override
    public void deleteMin() {
        if (isEmpty()) return;
        root = deleteMin(root);
    }

    private Node deleteMin(Node x) {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        x.n = size(x.left) + size(x.right) + 1;
        return x;
    }

    @Override
    public void deleteMax() {
        if (isEmpty()) return;
        root = deleteMax(root);
    }

    private Node deleteMax(Node x) {
        if (x.right == null) return null;
        x.right = deleteMax(x.right);
        x.n = 1 + size(x.left) + size(x.right);
        return x;
    }

    @Override
    public int size(Key lo, Key hi) {
        int size = rank(hi) - rank(lo);
        if (contains(hi)) return size + 1;
        return size;
    }

    @Override
    public Iterable<Key> keys(Key lo, Key hi) {
        Queue<Key> q = new Queue<>();
        keys(root, q, lo, hi);
        return q;
    }

    private void keys(Node x, Queue<Key> q, Key lo, Key hi) {
        if (x == null) return;
        int cmpLo = lo.compareTo(x.key);
        int cmpHi = hi.compareTo(x.key);
        if (cmpLo < 0) keys(x.left, q, lo, hi);
        if (cmpLo <= 0 && cmpHi >= 0) q.enqueue(x.key);
        if (cmpHi > 0) keys(x.right, q, lo, hi);
    }

    @Override
    public Iterable<Key> keys() {
        return keys(min(), max());
    }

    private class Node {
        private final Key key;
        private Value val;
        private Node left, right;
        private int n;

        public Node(Key key, Value val, int n) {
            this.key = key;
            this.val = val;
            this.n = n;
        }
    }
}
