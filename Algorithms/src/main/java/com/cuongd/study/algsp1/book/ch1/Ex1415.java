package com.cuongd.study.algsp1.book.ch1;

public class Ex1415 {
    public static int twoSumFaster(int[] a) {
        return twoSumEquals(a, 0, 0, a.length - 1);
    }

    public static int threeSumFaster(int[] a) {
        int count = 0;

        for (int i = 0; i < a.length - 2; i++) {
            if (a[i] >= 0) {
                break;
            }
            count += twoSumEquals(a, -a[i], i + 1, a.length - 1);
        }
        return count;
    }

    private static int twoSumEquals(int[] a, int target, int from, int to) {
        int count = 0;

        for (int i = from, j = to; i < j; ) {
            int sum = a[i] + a[j];
            if (sum == target) {
                count++;
                i++;
                j--;
            } else if (sum < target) {
                i++;
            } else {
                j--;
            }
        }
        return count;
    }
}
