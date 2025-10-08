package com.cuongd.study.leetcode;

class Lc3152 {
    public boolean[] isArraySpecial(int[] nums, int[][] queries) {
        int[] runningFaults = new int[nums.length];
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] % 2 == nums[i - 1] % 2) {
                runningFaults[i] = runningFaults[i - 1] + 1;
            } else {
                runningFaults[i] = runningFaults[i - 1];
            }
        }
        boolean[] ans = new boolean[queries.length];
        for (int i = 0; i < queries.length; i++) {
            int s = queries[i][0], e = queries[i][1];
            ans[i] = (runningFaults[s] - runningFaults[e]) == 0;
        }
        return ans;
    }
}
