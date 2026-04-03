package com.cuongd.study.ce.leetcode;

public class Lc1004 {
  public int longestOnes(int[] nums, int k) {
    int i = 0, j = 0, ans = 0;
    while (j < nums.length) {
      if (nums[j] == 1 || k > 0) {
        k -= 1 - nums[j];
        j++;
      } else {
        ans = Math.max(ans, j - i);
        while (k == 0) {
          k += 1 - nums[i++];
        }
      }
    }
    ans = Math.max(ans, nums.length - i);
    return ans;
  }

  public static void main(String[] args) {
    assert new Lc1004().longestOnes(new int[] {1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0}, 2) == 6;
    assert new Lc1004()
            .longestOnes(new int[] {0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1}, 3)
        == 10;
    assert new Lc1004().longestOnes(new int[] {0, 0, 1, 1, 1, 0, 0}, 0) == 3;
    assert new Lc1004().longestOnes(new int[] {0, 0, 0, 1}, 3) == 4;
  }
}
