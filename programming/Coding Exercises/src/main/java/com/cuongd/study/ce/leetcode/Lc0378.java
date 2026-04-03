package com.cuongd.study.ce.leetcode;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Lc0378 {
  public int kthSmallest(int[][] matrix, int k) {
    PriorityQueue<List<Integer>> buf =
        new PriorityQueue<>((o1, o2) -> Integer.compare(o1.get(0), o2.get(0)));
    for (int i = 0; i < matrix.length; i++) {
      List<Integer> x = new ArrayList<>();
      x.add(matrix[i][0]);
      x.add(i);
      x.add(0);
      buf.add(x);
    }
    while (true) {
      List<Integer> x = buf.remove();
      int ans = x.get(0);
      if (k == 1) return ans;
      int i = x.get(1);
      int j = x.get(2);
      if (j < matrix.length - 1) {
        List<Integer> next = new ArrayList<>();
        next.add(matrix[i][j + 1]);
        next.add(i);
        next.add(j + 1);
        buf.add(next);
      }
      k--;
    }
  }
}
