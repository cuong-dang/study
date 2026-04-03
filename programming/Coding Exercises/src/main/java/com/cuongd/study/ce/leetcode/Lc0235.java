package com.cuongd.study.ce.leetcode;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

class Lc0235 {
  private Map<TreeNode, TreeNode> pathTo;

  public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    pathTo = new HashMap<>();
    pathTo.put(root, null);
    bfs(root);
    Set<TreeNode> pAncs = new HashSet<>();
    for (TreeNode x = p; x != null; x = pathTo.get(x)) {
      pAncs.add(x);
    }
    for (TreeNode x = q; x != null; x = pathTo.get(x)) {
      if (pAncs.contains(x)) return x;
    }
    throw new AssertionError();
  }

  private void bfs(TreeNode root) {
    Queue<TreeNode> q = new ArrayDeque<>();
    q.add(root);
    while (!q.isEmpty()) {
      TreeNode x = q.remove();
      if (x.left != null && !pathTo.containsKey(x.left)) {
        q.add(x.left);
        pathTo.put(x.left, x);
      }
      if (x.right != null && !pathTo.containsKey(x.right)) {
        q.add(x.right);
        pathTo.put(x.right, x);
      }
    }
  }

  static class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
      val = x;
    }
  }
}
