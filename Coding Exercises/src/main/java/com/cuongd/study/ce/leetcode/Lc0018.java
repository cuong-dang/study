package com.cuongd.study.ce.leetcode;

import static com.cuongd.study.ce.Common.listOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Lc0018 {
  public List<List<Integer>> fourSum(int[] nums, int target) {
    int n = nums.length;
    Arrays.sort(nums);
    List<List<Integer>> res = new ArrayList<>();
    for (int i = 0; i < n; ++i) {
      for (int j = i + 1; j < n; ++j) {
        twoSum(res, nums, i, j, (long) target - nums[i] - nums[j]);
      }
    }
    return res.stream().distinct().collect(Collectors.toList());
  }

  private void twoSum(List<List<Integer>> res, int[] nums, int i, int j, long target) {
    Set<Long> seen = new HashSet<>();
    int n = nums.length;
    for (int k = j + 1; k < n; ++k) {
      long complement = target - nums[k];
      if (seen.contains(complement)) {
        res.add(listOf(nums[i], nums[j], nums[k], (int) complement));
      }
      seen.add((long) nums[k]);
    }
  }

  public static void main(String[] args) {
    System.out.println(
        new Lc0018()
            .fourSum(new int[] {1000000000, 1000000000, 1000000000, 1000000000}, -294967296));
  }
}
