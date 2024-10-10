package com.cuongd.study.algs.book.ch5;

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

public class NonRecursiveTrieST<Value> {
    private static final int R = 256;

    private static class Node {
        Object val;
        Node[] next = new Node[R];
    }

    private Node root;

    public void put(String key, Value val) {
        if (root == null) {
            root = new Node();
        }
        Node curr = root;
        for (int d = 0; d < key.length(); d++) {
            char c = key.charAt(d);
            if (curr.next[c] == null) {
                curr.next[c] = new Node();
            }
            curr = curr.next[c];
        }
        curr.val = val;
    }

    public Value get(String key) {
        Node curr = root;
        for (int d = 0; d < key.length(); d++) {
            if (curr == null) return null;
            char c = key.charAt(d);
            curr = curr.next[c];
        }
        if (curr == null) return null;
        return (Value) curr.val;
    }

    public void delete(String key) {
        Stack<Node> parents = new Stack<>();
        Stack<Character> childChars = new Stack<>();
        Node curr = root;
        int d;
        for (d = 0; d < key.length(); d++) {
            char c = key.charAt(d);
            if (curr.next[c] != null) {
                parents.push(curr);
                childChars.push(c);
                curr = curr.next[c];
            } else {
                return;
            }
        }
        curr.val = null;
        while (!parents.isEmpty()) {
            Node parent = parents.pop();
            char c = childChars.pop();
            if (isEmptyNode(curr)) {
                parent.next[c] = null;
            }
            curr = parent;
        }
        if (isEmptyNode(curr)) {
            root = null;
        }
    }

    private boolean isEmptyNode(Node x) {
        if (x.val != null) return false;
        for (char c = 0; c < R; c++) {
            if (x.next[c] != null) return false;
        }
        return true;
    }

    public boolean contains(String key) {
        return get(key) != null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public Iterable<String> keysWithPrefix(String prefix) {
        Node curr = root;
        // walk prefix
        for (int d = 0; d < prefix.length(); d++) {
            if (curr == null) break;
            curr = curr.next[prefix.charAt(d)];
        }
        Queue<String> result = new Queue<>();
        if (curr == null) return result;
        // collect keys
        Queue<String> searchStrings = new Queue<>();
        Queue<Node> searchNodes = new Queue<>();
        searchStrings.enqueue(prefix);
        searchNodes.enqueue(curr);
        while (!searchNodes.isEmpty()) {
            Node x = searchNodes.dequeue();
            String s = searchStrings.dequeue();
            if (x.val != null) {
                result.enqueue(s);
            }
            for (char c = 0; c < R; c++) {
                if (x.next[c] != null) {
                    searchNodes.enqueue(x.next[c]);
                    searchStrings.enqueue(s + c);
                }
            }
        }
        return result;
    }

    public Iterable<String> keysThatMatch(String s) {
        Queue<String> result = new Queue<>();
        Queue<Node> searchNodes = new Queue<>();
        Queue<String> searchString = new Queue<>();
        searchNodes.enqueue(root);
        searchString.enqueue("");
        while (!searchNodes.isEmpty()) {
            Node x = searchNodes.dequeue();
            String xs = searchString.dequeue();
            if (xs.length() == s.length()) {
                if (x.val != null) {
                    result.enqueue(xs);
                }
                continue;
            }
            char c = s.charAt(xs.length());
            for (char d = 0; d < R; d++) {
                if ((c == '.' || c == d) && x.next[d] != null) {
                    searchNodes.enqueue(x.next[d]);
                    searchString.enqueue(xs + d);
                }
            }
        }
        return result;
    }

    public Iterable<String> keys() {
        return keysWithPrefix("");
    }

    public static void main(String[] args) {
        NonRecursiveTrieST<Integer> t = new NonRecursiveTrieST<>();
        t.put("she", 0);
        assert !t.isEmpty();
        t.delete("ehs");
        assert t.get("she") == 0;
        t.delete("she");
        assert t.isEmpty();
        t.put("she", 0);
        t.put("sells", 1);
        t.put("sea", 2);
        t.put("shells", 3);
        t.put("by", 4);
        t.put("the", 5);
        assert t.get("sea") == 2;
        t.put("sea", 6);
        t.put("shore", 7);
        assert t.get("sells") == 1;
        assert t.get("shells") == 3;
        assert t.get("by") == 4;
        assert t.get("the") == 5;
        assert t.get("sea") == 6;
        assert t.get("shore") == 7;
        System.out.println(t.keysWithPrefix("sh"));

        t.delete("shells");
        assert !t.contains("shells");
        assert t.contains("she");
        t.put("shells", 3);
        t.delete("she");
        assert t.contains("shells");
        assert !t.contains("she");
        t.put("she", 0);
        System.out.println(t.keysThatMatch(".he"));
    }
}
