package com.cuongd.study.leetcode;

public class Lc0061 {
  public ListNode rotateRight(ListNode head, int k) {
    if (head == null || k == 0) return head;

    int n = 0;
    ListNode x, p = null;
    for (x = head; x != null; p = x, x = x.next) {
      ++n;
    }
    k %= n;
    if (k == 0) return head;
    p.next = head;
    for (int i = 0; i < n - k; ++i) {
      p = head;
      head = head.next;
    }
    p.next = null;
    return head;
  }

  public static class ListNode {
    int val;
    ListNode next;

    ListNode() {}

    ListNode(int val) {
      this.val = val;
    }

    ListNode(int val, ListNode next) {
      this.val = val;
      this.next = next;
    }
  }
}
