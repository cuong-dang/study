package com.cuongd.study.leetcode;

import java.util.ArrayDeque;
import java.util.Queue;

public class Lc1004 {
    public int longestOnes(int[] nums, int k) {
        int start = 0, max = 0;
        Queue<Integer> flipped = new ArrayDeque<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 1) continue;
            if (flipped.size() < k) {
                flipped.add(i);
                continue;
            }
            if (i - start > max) {
                max = i - start;
            }
            start = flipped.isEmpty() ? i + 1 : flipped.remove() + 1;
            if (flipped.size() < k) {
                flipped.add(i);
            }
        }
        if (nums.length - start > max) {
            max = nums.length - start;
        }
        return max;
    }

    public static void main(String[] args) {
        assert new Lc1004().longestOnes(new int[]{1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0}, 2) == 6;
        assert new Lc1004().longestOnes(new int[]{0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1}, 3) == 10;
        assert new Lc1004().longestOnes(new int[]{0, 0, 1, 1, 1, 0, 0}, 0) == 3;
        assert new Lc1004().longestOnes(new int[]{0, 0, 0, 1}, 3) == 4;
    }
}
