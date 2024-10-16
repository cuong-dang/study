package com.cuongd.study.algs.book.ch5;

import edu.princeton.cs.algs4.Queue;

public class TST<Value> {
    private class Node {
        char c;
        Node left, mid, right;
        Value val;
    }

    private Node root;

    public Value get(String key) {
        Node x = get(root, key, 0);
        if (x == null) return null;
        return x.val;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        char c = key.charAt(d);
        if (c < x.c) return get(x.left, key, d);
        if (c > x.c) return get(x.right, key, d);
        if (d < key.length() - 1) return get(x.mid, key, d+1);
        return x;
    }

    public void put(String key, Value val) {
        root = put(root, key, val, 0);
    }

    private Node put(Node x, String key, Value val, int d) {
        char c = key.charAt(d);
        if (x == null) { x = new Node(); x.c = c; }
        if (c < x.c) x.left = put(x.left, key, val, d);
        else if (c > x.c) x.right = put(x.right, key, val, d);
        else if (d < key.length() - 1) x.mid = put(x.mid, key, val, d+1);
        else x.val = val;
        return x;
    }

    public Iterable<String> keys() {
        Queue<String> q = new Queue<>();
        collect(root, "", q);
        return q;
    }

    private void collect(Node x, String key, Queue<String> q) {
        if (x == null) return;
        if (x.val != null) {
            q.enqueue(key + x.c);
        }
        collect(x.left, key, q);
        collect(x.mid, key + x.c, q);
        collect(x.right, key, q);
    }

    public static void main(String[] args) {
        TST<Integer> t = new TST<>();
        t.put("shells", 15);
        t.put("by", 4);
        t.put("the", 8);
        t.put("are", 12);
        t.put("sells", 11);
        t.put("sea", 14);
        t.put("surely", 13);
        t.put("she", 10);
        t.put("shore", 7);
        t.keys().forEach(System.out::println);
    }
}
