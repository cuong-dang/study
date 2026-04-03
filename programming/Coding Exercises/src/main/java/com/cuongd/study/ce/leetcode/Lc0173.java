package com.cuongd.study.ce.leetcode;

import java.util.ArrayDeque;
import java.util.Queue;

public class Lc0173 {
  class BSTIterator {
    private Queue<Integer> q;

    public BSTIterator(TreeNode root) {
      q = new ArrayDeque<>();
      walk(root);
    }

    private void walk(TreeNode x) {
      if (x == null) return;
      walk(x.left);
      q.add(x.val);
      walk(x.right);
    }

    public int next() {
      return q.remove();
    }

    public boolean hasNext() {
      return !q.isEmpty();
    }
  }

  private static class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {}

    TreeNode(int val) {
      this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
      this.val = val;
      this.left = left;
      this.right = right;
    }
  }
}
