package com.cuongd.study.ce.leetcode;

public class Lc1833 {
  public int maxIceCream(int[] costs, int coins) {
    sort(costs);
    int res = 0;
    for (int i = 0; i < costs.length && costs[i] <= coins; i++) {
      res++;
      coins -= costs[i];
    }
    return res;
  }

  private void sort(int[] a) {
    int max = a[0];
    for (int e : a) {
      if (e > max) {
        max = e;
      }
    }

    int[] count = new int[max + 2];
    for (int e : a) {
      count[e + 1]++;
    }
    for (int i = 0; i < count.length - 1; i++) {
      count[i + 1] += count[i];
    }
    int[] aux = new int[a.length];
    for (int j : a) {
      aux[count[j]++] = j;
    }
    System.arraycopy(aux, 0, a, 0, a.length);
  }
}
