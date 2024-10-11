package com.cuongd.study.algs.book.ch5;

import edu.princeton.cs.algs4.Queue;

public class TrieST<Value> {
    private final static int R = 256;
    private Node root = new Node();

    private static class Node {
        private Object val;
        private Node[] next = new Node[R];
    }

    public Value get(String key) {
        Node x = get(root, key, 0);
        if (x == null) return null;
        return (Value) x.val;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next[c], key, d+1);
    }

    public void put(String key, Value val) {
        root = put(root, key, val, 0);
    }

    private Node put(Node x, String key, Value val, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) { x.val = val; return x; }
        char c = key.charAt(d);
        x.next[c] = put(x.next[c], key, val, d+1);
        return x;
    }

    public Iterable<String> keys() {
        return keysWithPrefix("");
    }

    public Iterable<String> keysWithPrefix(String prefix) {
        Queue<String> queue = new Queue<>();
        collect(get(root, prefix, 0), prefix, queue);
        return queue;
    }

    private void collect(Node x, String prefix, Queue<String> queue) {
        if (x == null) return;
        if (x.val != null) queue.enqueue(prefix);
        for (char c = 0; c < R; c++) {
            collect(x.next[c], prefix + c, queue);
        }
    }

    public Iterable<String> keysThatMatch(String pattern) {
        Queue<String> queue = new Queue<>();
        collect(root, "", pattern, queue);
        return queue;
    }

    private void collect(Node x, String prefix, String pattern, Queue<String> queue) {
        if (x == null) return;
        int d = prefix.length();
        if (d == pattern.length()) {
            if (x.val != null) {
                queue.enqueue(prefix);
            }
            return;
        }
        char next = pattern.charAt(d);
        for (char c = 0; c < R; c++) {
            if (next == '.' || c == next) {
                collect(x.next[c], prefix + c, pattern, queue);
            }
        }
    }

    public String longestPrefixOf(String s) {
        int length = search(root, s, 0, 0);
        return s.substring(0, length);
    }

    private int search(Node x, String s, int d, int length) {
        if (x == null) return length;
        if (x.val != null) length = d;
        if (d == s.length()) return length;
        char c = s.charAt(d);
        return search(x.next[c], s, d+1, length);
    }

    public void delete(String key) {
        root = delete(root, key, 0);
    }

    private Node delete(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) {
            x.val = null;
        } else {
            char c = key.charAt(d);
            x.next[c] = delete(x.next[c], key, d+1);
        }
        if (x.val != null) return x;
        for (char c = 0; c < R; c++) {
            if (x.next[c] != null) return x;
        }
        return null;
    }

    public String floor(String s) {
        StringBuilder result = new StringBuilder();
        floor(root, s, 0, result);
        return result.toString();
    }

    private boolean floor(Node x, String s, int d, StringBuilder result) {
        if (d == s.length()) {
            return x.val != null;
        }
        char c = s.charAt(d);
        if (x.next[c] != null) {
            result.append(c);
            if (floor(x.next[c], s, d+1, result)) {
                return true;
            }
            result.deleteCharAt(result.length()-1);
        }
        for (int i = c-1; i >= 0; i--) {
            if (x.next[i] != null) {
                result.append(max(x.next[i], String.format("%c", (char) i)));
                return true;
            }
        }
        return false;
    }

    public String max() {
        return max(root, "");
    }

    private String max(Node x, String key) {
        int next = -1;
        for (int r = R-1; r >= 0; r--) {
            if (x.next[r] != null) {
                next = r;
                break;
            }
        }
        if (next != -1) {
            return max(x.next[next], key + (char) next);
        }
        return key;
    }

    public static void main(String[] args) {
        TrieST<Integer> t = new TrieST<>();
        t.put("she", 0);
        t.put("sells", 1);
        t.put("sea", 2);
        t.put("shells", 3);
        t.put("by", 4);
        t.put("the", 5);
        t.put("sea", 6);
        t.put("shore", 7);

        assert t.max().equals("the");
        assert t.floor("shorf").equals("shore");
        assert t.floor("shells").equals("shells");
        assert t.floor("shellr").equals("sells");
        assert t.floor("shell").equals("sells");
        assert t.floor("shf").equals("shells");
    }
}
