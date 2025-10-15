package com.cuongd.study.ce.leetcode;

import static com.cuongd.study.ce.Common.listOf;
import static com.cuongd.study.ce.Common.setOf;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Lc0863 {
  public List<Integer> distanceK(TreeNode root, TreeNode target, int k) {
    Graph G = new Graph();
    translate(root, G);
    Set<Integer> seen = new HashSet<>(setOf(target.val));
    Queue<Integer> q = new ArrayDeque<>();
    q.add(target.val);
    int steps = 0;
    while (true) {
      if (q.isEmpty()) return listOf();
      if (steps == k) {
        return new ArrayList<>(q);
      }
      Queue<Integer> newQ = new ArrayDeque<>();
      while (!q.isEmpty()) {
        int v = q.remove();
        seen.add(v);
        for (int w : G.adj(v)) {
          if (!seen.contains(w)) {
            newQ.add(w);
          }
        }
      }
      steps++;
      q = newQ;
    }
  }

  private void translate(TreeNode x, Graph G) {
    G.addVertex(x.val);
    if (x.left != null) {
      G.addVertex(x.left.val);
      G.addEdge(x.val, x.left.val);
      translate(x.left, G);
    }
    if (x.right != null) {
      G.addVertex(x.right.val);
      G.addEdge(x.val, x.right.val);
      translate(x.right, G);
    }
  }

  private static class Graph {
    Map<Integer, List<Integer>> adj;

    public Graph() {
      adj = new HashMap<>();
    }

    public void addVertex(int v) {
      if (!adj.containsKey(v)) {
        adj.put(v, new ArrayList<>());
      }
    }

    public void addEdge(int v, int w) {
      adj.get(v).add(w);
      adj.get(w).add(v);
    }

    public List<Integer> adj(int v) {
      return adj.get(v);
    }
  }

  private static class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
      val = x;
    }
  }
}
