package com.cuongd.study.algs.coursera.wk8;

import java.util.Objects;

public class BoggleTrie {
    private static final int R = 26;
    private final Node root;

    public BoggleTrie() {
        root = new Node();
    }

    public void add(String s) {
        Objects.requireNonNull(search(s, true)).setIsString();
    }

    public boolean contains(String s) {
        Node x = search(s, false);
        return x != null && x.isString;
    }

    public boolean containsPrefix(String s) {
        return search(s, false) != null;
    }

    public Node searchPrefix(String s, Node fromNode) {
        if (fromNode == null) return search(s, false);
        if (s.charAt(s.length()-2) == 'Q') {
            int q = 'Q' - 'A', u = 'U' - 'A';
            if (fromNode.next[q] == null) return null;
            return fromNode.next[q].next[u];
        }
        return fromNode.next[s.charAt(s.length()-1) - 'A'];
    }

    private Node search(String s, boolean allocating) {
        int d = 0;
        Node x = root;
        while (d < s.length()) {
            int c = s.charAt(d) - 'A';
            if (x.next[c] == null) {
                if (!allocating) return null;
                x.next[c] = new Node();
            }
            x = x.next[c];
            d++;
        }
        return x;
    }

    public static class Node {
        private final Node[] next;
        private boolean isString;

        public Node() {
            next = new Node[R];
            isString = false;
        }

        public void setIsString() {
            isString = true;
        }
    }

    public static void main(String[] args) {
        BoggleTrie bt = new BoggleTrie();
        assert !bt.contains("A");
        bt.add("A");
        assert bt.contains("A");
        assert bt.containsPrefix("A");
        bt.add("AB");
        assert bt.contains("A");
        assert bt.containsPrefix("A");
        assert bt.contains("AB");
        assert bt.containsPrefix("AB");
        bt.add("CD");
        assert !bt.contains("C");
        assert bt.containsPrefix("C");
        assert bt.contains("CD");
        assert bt.containsPrefix("CD");
    }
}
