package com.cuongd.study.alspe.c3.pa3;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

class HuffmanEncoder<K> {
    private final Queue<Node> pq;
    private Node root;
    private final Map<K, String> keyCode;

    public HuffmanEncoder() {
        pq = new PriorityQueue<>();
        keyCode = new HashMap<>();
    }

    public void add(K key, double weight) {
        pq.add(new Node(key, weight));
    }

    public void encode() {
        if (pq.isEmpty()) return;
        while (pq.size() > 1) {
            Node left = pq.remove(), right = pq.remove();
            Node merged = new Node(left.weight + right.weight, left, right);
            pq.add(merged);
        }
        root = pq.remove();
        assignBitCodes();
    }

    public String codeOf(K key) {
        return keyCode.get(key);
    }

    private void assignBitCodes() {
        StringBuilder sb = new StringBuilder();
        dfs(root, sb);
    }

    private void dfs(Node x, StringBuilder sb) {
        if (x.left == null && x.right == null) {
            assert x.key != null;
            keyCode.put(x.key, sb.toString());
            return;
        }
        if (x.left != null) {
            sb.append('0');
            dfs(x.left, sb);
            sb.deleteCharAt(sb.length() - 1);
        }
        if (x.right != null) {
            sb.append('1');
            dfs(x.right, sb);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    private class Node implements Comparable<Node> {
        public final K key;
        public final double weight;
        public final Node left;
        public final Node right;

        public Node(double weight, Node left, Node right) {
            this.key = null;
            this.weight = weight;
            this.left = left;
            this.right = right;
        }

        public Node(K key, double weight) {
            this.key = key;
            this.weight = weight;
            this.left = null;
            this.right = null;
        }

        @Override
        public int compareTo(Node that) {
            return Double.compare(this.weight, that.weight);
        }
    }

    public static void main(String[] args) {
        HuffmanEncoder<String> he = new HuffmanEncoder<>();
        he.add("a", .32);
        he.add("b", .25);
        he.add("c", .2);
        he.add("d", .18);
        he.add("e", .05);
        he.encode();
        assert he.codeOf("a").equals("11");
        assert he.codeOf("b").equals("10");
        assert he.codeOf("c").equals("00");
        assert he.codeOf("d").equals("011");
        assert he.codeOf("e").equals("010");
    }
}
