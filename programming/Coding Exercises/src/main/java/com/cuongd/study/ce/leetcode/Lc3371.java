package com.cuongd.study.ce.leetcode;

import java.util.HashMap;
import java.util.Map;

public class Lc3371 {
  public int getLargestOutlier(int[] nums) {
    Map<Integer, Integer> count = new HashMap<>();
    int sum = 0;
    for (int num : nums) {
      sum += num;
      count.put(num, count.getOrDefault(num, 0) + 1);
    }
    int ans = Integer.MIN_VALUE;
    for (int num : nums) {
      int without = sum - num;
      if (without % 2 != 0) continue;
      int other = without / 2;
      if (count.containsKey(other)) {
        if ((other != num || count.get(other) > 1) && num > ans) ans = num;
      }
    }
    return ans;
  }
}
