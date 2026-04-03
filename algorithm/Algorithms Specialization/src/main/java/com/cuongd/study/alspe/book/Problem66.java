package com.cuongd.study.alspe.book;

import java.util.Random;

import static com.cuongd.study.alspe.Partition.partition;

class Problem66 {
    private static int solve(int[] a) {
        int W = sumWeight(a, 0, a.length - 1) / 2;
        return weightedMedian(a, 0, a.length - 1, W, W);
    }

    private static int weightedMedian(int[] a, int lo, int hi, int maxLeftWeight, int maxRightWeight) {
        assert maxLeftWeight >= 0;
        assert maxRightWeight >= 0;
        int p = new Random().nextInt(hi - lo + 1) + lo;
        int j = partition(a, lo, hi, p);
        int leftWeight = sumWeight(a, lo, j - 1);
        int rightWeight = sumWeight(a, j + 1, hi);
        if (leftWeight <= maxLeftWeight && rightWeight <= maxRightWeight) return a[j];
        if (leftWeight > maxLeftWeight) {
            return weightedMedian(a, lo, j - 1, maxLeftWeight, maxRightWeight - rightWeight - a[j]);
        }
        return weightedMedian(a, j + 1, hi, maxLeftWeight - leftWeight - a[j], maxRightWeight);
    }

    private static int sumWeight(int[] a, int lo, int hi) {
        int s = 0;
        for (int i = lo; i <= hi; i++) {
            s += a[i];
        }
        return s;
    }

    public static void main(String[] args) {
        assert solve(new int[]{3, 2, 4}) == 3;
        assert solve(new int[]{1, 5, 2}) == 5;
        assert solve(new int[]{2, 3, 1, 10, 6}) == 6;
    }
}
