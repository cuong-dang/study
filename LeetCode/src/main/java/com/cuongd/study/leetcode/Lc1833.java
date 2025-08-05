package com.cuongd.study.leetcode;

import java.util.Arrays;

public class Lc1833 {
    public int maxIceCream(int[] costs, int coins) {
        Arrays.sort(costs);
        int res = 0;
        for (int i = 0; i < costs.length && costs[i] <= coins; i++) {
            res++;
            coins -= costs[i];
        }
        return res;
    }
}
