package com.cuongd.study.algsp1.book.ch3;

import edu.princeton.cs.algs4.Queue;

public class Ex313<Key extends Comparable<Key>, Value> implements OrderedST<Key, Value> {
    private Node head = null;
    private int n = 0;

    public Ex313() {}

    public void put(Key key, Value val) {
        if (head == null) {
            head = new Node(key, val, null);
            n = 1;
            return;
        }

        Node curr = head, prev = null;
        while (curr != null && key.compareTo(curr.key) > 0) {
            prev = curr;
            curr = curr.next;
        }
        if (curr == null) {
            curr = new Node(key, val, null);
            prev.next = curr;
            ++n;
        } else if (key.compareTo(curr.key) == 0) {
            curr.val = val;
        } else {
            curr = new Node(key, val, curr);
            if (prev == null)
                head = curr;
            else
                prev.next = curr;
            ++n;
        }
    }

    public Value get(Key key) {
        for (Node curr = head; curr != null; curr = curr.next)
            if (key.compareTo(curr.key) == 0)
                return curr.val;
        return null;
    }

    public void delete(Key key) {
        Node curr = head, prev = null;
        while (curr != null && key.compareTo(curr.key) > 0) {
            prev = curr;
            curr = curr.next;
        }
        if (curr == null)
            return;
        if (key.compareTo(curr.key) == 0) {
            if (prev == null)
                head = curr.next;
            else {
                prev.next = curr.next;
            }
            --n;
        }
    }

    public boolean contains(Key key) {
        Node curr;
        for (curr = head; curr != null; curr = curr.next)
            if (key.compareTo(curr.key) == 0)
                break;
        return !(curr == null);
    }

    public boolean isEmpty() {
        return n == 0;
    }

    public int size() {
        return n;
    }

    public Key min() {
        if (head != null)
            return head.key;
        return null;
    }

    public Key max() {
        Node x = head;
        while (x != null && x.next != null)
            x = x.next;
        if (x != null)
            return x.key;
        return null;
    }

    public Key floor(Key key) {
        Node x = head, prev = null;
        while (x != null && (x.key.compareTo(key) < 0 || x.key.compareTo(key) == 0)) {
            prev = x;
            x = x.next;
        }
        if (prev == null)
            return null;
        return prev.key;
    }

    public Key ceiling(Key key) {
        Node x = head;
        while (x != null && x.key.compareTo(key) < 0) {
            x = x.next;
        }
        if (x == null || x.key.compareTo(key) < 0)
            return null;
        return x.key;
    }

    public int rank(Key key) {
        int rank = 0;
        for (Node x = head; x != null; x = x.next)
            if (key.compareTo(x.key) > 0)
                ++rank;
            else
                break;
        return rank;
    }

    public Key select(int k) {
        Node x = head;
        while (k > 0) {
            x = x.next;
            --k;
        }
        return x.key;
    }

    public void deleteMin() {
        delete(min());
    }

    public void deleteMax() {
        delete(max());
    }

    public int size(Key lo, Key hi) {
        int result = 0;
        for (Node x = head; x != null && x.key.compareTo(hi) <= 0; x = x.next)
            if (x.key.compareTo(lo) >= 0)
                ++result;
        return result;
    }

    public Iterable<Key> keys(Key lo, Key hi) {
        Queue<Key> q = new Queue<>();
        for (Node x = head; x != null && x.key.compareTo(hi) <= 0; x = x.next)
            if (x.key.compareTo(lo) >= 0)
                q.enqueue(x.key);
        return q;
    }

    public Iterable<Key> keys() {
        return keys(min(), max());
    }

    private class Node {
        Key key;
        Value val;
        Node next;

        public Node(Key key, Value val, Node next) {
            this.key = key;
            this.val = val;
            this.next = next;
        }
    }
}
