package com.cuongd.study.ce.leetcode;

import static com.cuongd.study.ce.Common.*;

import java.util.Arrays;

public class Lc0324 {
  public void wiggleSort(int[] nums) {
    int n = nums.length, m = (n + 1) / 2;
    if (n == 1) return;
    int[] aux = Arrays.copyOf(nums, n);
    int median = select(aux, m);

    partition(aux, median);

    for (int i = m - 1, j = 0; i >= 0; i--, j += 2) nums[j] = aux[i];
    for (int i = n - 1, j = 1; i >= m; i--, j += 2) nums[j] = aux[i];
  }

  public static void main(String[] args) {
    new Lc0324().wiggleSort(new int[] {1});
  }
}
