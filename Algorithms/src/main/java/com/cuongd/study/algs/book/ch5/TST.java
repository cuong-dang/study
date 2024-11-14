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
        return keysWithPrefix("");
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

    public String longestPrefixOf(String s) {
        int length = search(root, s, 0, 0);
        return s.substring(0, length);
    }

    private int search(Node x, String s, int d, int length) {
        if (x == null) return length;
        char c = s.charAt(d);
        if (c < x.c) return search(x.left, s, d, length);
        if (c > x.c) return search(x.right, s, d, length);
        if (x.val != null) length = d+1;
        return search(x.mid, s, d+1, length);
    }

    public Iterable<String> keysWithPrefix(String s) {
        Queue<String> q = new Queue<>();
        keysWithPrefix(root, s, 0, "", q);
        return q;
    }

    private void keysWithPrefix(Node x, String s, int d, String key,
                                Queue<String> q) {
        if (x == null) return;
        if (d == s.length()) {
            collect(x, key, q);
            return;
        }
        char c = s.charAt(d);
        if (c == x.c) {
            keysWithPrefix(x.mid, s, d + 1, key + c, q);
        } else if (c < x.c) {
            keysWithPrefix(x.left, s, d, key, q);
        } else {
            keysWithPrefix(x.right, s, d, key, q);
        }
    }

    public Iterable<String> keysThatMatch(String s) {
        Queue<String> q = new Queue<>();
        keysThatMatch(root, s, 0, "", q);
        return q;
    }

    private void keysThatMatch(Node x, String s, int d, String key,
                               Queue<String> q) {
        if (x == null) return;
        char c = s.charAt(d);
        if (c == '.') {
            if (x.left != null) {
                keysThatMatch(x.left.mid, s, d + 1, key + x.left.c, q);
            }
            keysThatMatch(x.mid, s, d + 1, key + x.c, q);
            if (x.right != null) {
                keysThatMatch(x.right.mid, s, d + 1, key + x.right.c,
                        q);
            }
        } else if (c < x.c) {
            keysThatMatch(x.left, s, d, key, q);
        } else if (c > x.c) {
            keysThatMatch(x.right, s, d, key, q);
        } else {
            if (d == s.length() - 1 && x.val != null) q.enqueue(key + x.c);
            else if (d < s.length() - 1) {
                keysThatMatch(x.mid, s, d + 1, key + x.c, q);
            }
        }
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

        assert t.longestPrefixOf("seashore").equals("sea");
        assert t.longestPrefixOf("shellsort").equals("shells");
        assert t.longestPrefixOf("c").isEmpty();

        t.keysThatMatch("shell").forEach(System.out::println);
    }
}
