package com.cuongd.study.leetcode;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

class Lc0116 {
    private Map<Integer, Queue<Node>> collector;

    public Node connect(Node root) {
        collector = new HashMap<>();
        collect(root, 0);
        for (int level : collector.keySet()) {
            Queue<Node> q = collector.get(level);
            Node x = q.remove();
            while (!q.isEmpty()) {
                x.next = q.remove();
                x = x.next;
            }
        }
        return root;
    }

    private void collect(Node x, int level) {
        if (x == null) return;
        if (!collector.containsKey(level)) {
            collector.put(level, new ArrayDeque<>());
        }
        collector.get(level).add(x);
        collect(x.left, level + 1);
        collect(x.right, level + 1);
    }

    private class Node {
        public int val;
        public Node left;
        public Node right;
        public Node next;

        public Node() {
        }

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, Node _left, Node _right, Node _next) {
            val = _val;
            left = _left;
            right = _right;
            next = _next;
        }
    }
}
