package com.cuongd.study.ce.leetcode;

import java.util.HashMap;

public class Lc0523 {
  public boolean checkSubarraySum(int[] nums, int k) {
    int runningSum = 0;
    HashMap<Integer, Integer> R = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
      runningSum += nums[i];
      if (runningSum % k == 0 && i != 0) return true;
      int r = runningSum % k;
      if (i - R.getOrDefault(r, i) > 1) return true;
      if (!R.containsKey(r)) {
        R.put(r, i);
      }
    }
    return false;
  }
}
