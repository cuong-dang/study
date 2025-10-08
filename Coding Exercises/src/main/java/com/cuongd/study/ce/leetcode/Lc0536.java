package com.cuongd.study.ce.leetcode;

class Lc0536 {
  private String s;
  private int i;

  public TreeNode str2tree(String s) {
    if (s.length() == 0) return null;
    this.s = s + ")";
    i = 0;
    return parseTree();
  }

  private TreeNode parseTree() {
    if (i == s.length()) return null;
    int val = parseVal();
    if (s.charAt(i++) != '(') {
      i--;
      return new TreeNode(val, null, null);
    }
    TreeNode left = parseTree();
    if (s.charAt(i++) != ')') throw new AssertionError();
    if (s.charAt(i++) != '(') {
      i--;
      return new TreeNode(val, left, null);
    }
    TreeNode right = parseTree();
    if (s.charAt(i++) != ')') throw new AssertionError();
    return new TreeNode(val, left, right);
  }

  private int parseVal() {
    StringBuilder sb = new StringBuilder();
    while (s.charAt(i) == '-' || Character.isDigit(s.charAt(i))) {
      sb.append(s.charAt(i++));
    }
    return Integer.parseInt(sb.toString());
  }

  private class TreeNode {
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
