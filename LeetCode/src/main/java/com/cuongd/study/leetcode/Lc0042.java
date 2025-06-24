package com.cuongd.study.leetcode;

import java.util.Stack;

class Lc0042 {
    public int trap(int[] height) {
        int ans = 0, i = 0;
        while (i < height.length && height[i] == 0) {
            i++;
        }
        Stack<Integer> t = new Stack<>();
        while (true) {
            if (i >= height.length - 2) {
                break;
            }
            int j = i + 1;
            t.clear();
            int tMax = height[j];
            while (j < height.length && height[j] < height[i]) {
                t.add(height[i] - height[j]);
                tMax = Math.max(tMax, height[j]);
                j++;
            }
            if (j < height.length) {
                ans += t.stream().reduce(0, (a, b) -> a + b);
                i = j;
            } else {
                height[i] = tMax;
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        assert new Lc0042().trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}) == 6;
        assert new Lc0042().trap(new int[]{4, 2, 0, 3, 2, 5}) == 9;
        assert new Lc0042().trap(new int[]{4, 2, 3}) == 1;
    }
}
