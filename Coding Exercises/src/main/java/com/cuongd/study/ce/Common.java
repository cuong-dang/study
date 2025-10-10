package com.cuongd.study.ce;

import java.util.Arrays;

public class Common {
  public static int select(int[] a, int k) {
    if (k < 0 || k > a.length - 1) throw new AssertionError();
    int[] aux = Arrays.copyOf(a, a.length);
    return select(aux, k, 0, a.length - 1);
  }

  private static int select(int[] a, int k, int lo, int hi) {
    int n = hi - lo + 1;
    if (n == 1) return a[lo];

    int j = partition(a, lo, hi, lo + n / 2);
    int order = j - lo;
    if (order == k) return a[j];
    if (order > k) return select(a, k, lo, j - 1);
    return select(a, k - order - 1, j + 1, hi);
  }

  public static void swap(int[] a, int i, int j) {
    int t = a[i];
    a[i] = a[j];
    a[j] = t;
  }

  public static int partition(int[] a, int lo, int hi, int p) {
    swap(a, lo, p);
    int i = lo + 1;
    for (int j = lo + 1; j <= hi; j++) {
      if (a[j] > a[lo]) continue;
      swap(a, j, i++);
    }
    swap(a, lo, i - 1);
    return i - 1;
  }

  public static void main(String[] args) {
    assert select(new int[] {1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 0) == 0;
    assert select(new int[] {1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 1) == 1;
    assert select(new int[] {1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 2) == 2;
    assert select(new int[] {1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 3) == 3;
    assert select(new int[] {1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 4) == 4;
    assert select(new int[] {1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 5) == 5;
    assert select(new int[] {1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 6) == 6;
    assert select(new int[] {1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 7) == 7;
    assert select(new int[] {1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 8) == 8;
    assert select(new int[] {1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 9) == 9;
  }
}
