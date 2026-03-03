package com.cuongd.study.ce.leetcode;

public class Lc0053 {
  public int maxSubArray(int[] nums) {
    if (nums.length == 1) return nums[0];
    int j = 1;
    int ans = nums[0];
    int sum = nums[0];
    while (j < nums.length) {
      if (sum < 0) {
        sum = nums[j];
        ans = Math.max(ans, sum);
      } else { // sum > 0
        sum += nums[j];
        ans = Math.max(ans, sum);
      }
      j++;
    }
    return ans;
  }
}
