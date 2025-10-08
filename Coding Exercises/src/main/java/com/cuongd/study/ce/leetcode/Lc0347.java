package com.cuongd.study.ce.leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class Lc0347 {
  public int[] topKFrequent(int[] nums, int k) {
    // count
    Map<Integer, Integer> count = new HashMap<>();
    for (int n : nums) {
      count.put(n, count.getOrDefault(n, 0) + 1);
    }
    Mode[] a = new Mode[count.size()];
    int i = 0;
    for (int n : count.keySet()) {
      a[i++] = new Mode(n, count.get(n));
    }
    // solve
    int lo = 0, hi = a.length - 1;
    int t = k;
    while (true) {
      if (hi <= lo) break;
      int j = partition(a, lo, hi);
      int got = j - lo + 1;
      if (got == t) break;
      if (got < t) {
        lo = j + 1;
        t -= got;
      } else {
        hi = j - 1;
      }
    }
    int[] ans = new int[k];
    for (int j = 0; j < k; j++) {
      ans[j] = a[j].v;
    }
    return ans;
  }

  private int partition(Mode[] a, int lo, int hi) {
    Mode v = a[lo];
    int i = lo, j = hi + 1;
    while (true) {
      while (a[++i].gt(v)) if (i == hi) break;
      while (v.gt(a[--j])) if (j == lo) break;
      if (i >= j) break;
      exch(a, i, j);
    }
    exch(a, lo, j);
    return j;
  }

  private void exch(Mode[] a, int i, int j) {
    Mode t = a[i];
    a[i] = a[j];
    a[j] = t;
  }

  private static class Mode {
    public final int v;
    public final int f;

    public Mode(int v, int f) {
      this.v = v;
      this.f = f;
    }

    public boolean gt(Mode that) {
      return this.f > that.f;
    }
  }

  public static void main(String[] args) {
    System.out.println(
        Arrays.toString(
            new Lc0347().topKFrequent(new int[] {-1, 1, 4, -4, 3, 5, 4, -2, 3, -1}, 3)));
  }
}
