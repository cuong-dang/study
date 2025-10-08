package com.cuongd.study.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Lc0133 {
    public Node cloneGraph(Node node) {
        if (node == null) return null;
        HashMap<Integer, Node> byVal = new HashMap<>();
        return cloneNode(node, byVal);
    }

    private Node cloneNode(Node node, HashMap<Integer, Node> byVal) {
        if (!byVal.containsKey(node.val)) {
            byVal.put(node.val, new Node(node.val));
            Node newNode = byVal.get(node.val);
            for (Node adj : node.neighbors) {
                newNode.neighbors.add(cloneNode(adj, byVal));
            }
            return newNode;
        }
        return byVal.get(node.val);
    }

    private static class Node {
        public int val;
        public List<Node> neighbors;

        public Node() {
            val = 0;
            neighbors = new ArrayList<>();
        }

        public Node(int _val) {
            val = _val;
            neighbors = new ArrayList<>();
        }

        public Node(int _val, ArrayList<Node> _neighbors) {
            val = _val;
            neighbors = _neighbors;
        }
    }
}
