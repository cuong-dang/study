package com.cuongd.study.leetcode;

import java.util.List;

public class Lc3148 {
    public int maxScore(List<List<Integer>> A) {
        int res = Integer.MIN_VALUE, m = A.size(), n = A.get(0).size();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int pre = Math.min(
                        i > 0 ? A.get(i - 1).get(j) : Integer.MAX_VALUE,
                        j > 0 ? A.get(i).get(j - 1) : Integer.MAX_VALUE
                );
                res = Math.max(res, A.get(i).get(j) - pre);
                if (pre < A.get(i).get(j)) {
                    A.get(i).set(j, pre);
                }
            }
        }
        return res;
    }
}
