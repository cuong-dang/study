package com.cuongd.study.algsp1.book.ch1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Ex1417 {
    public static Set<Double> findFarthestPair(double[] a) {
        double left, right;

        left = min(a[0], a[1]);
        right = max(a[0], a[1]);
        for (int i = 1; i < a.length; i++) {
            if (a[i] < left) {
                left = a[i];
            } else if (a[i] > right) {
                right = a[i];
            }
        }
        return new HashSet<>(Arrays.asList(left, right));
    }
}
