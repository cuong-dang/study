package com.cuongd.study.algsp1.book.ch1;

import java.util.ArrayList;
import java.util.List;

public class Ex1412 {
    public static List<Integer> findIntersects(int[] a, int[] b) {
        List<Integer> result = new ArrayList<>();

        for (int i = 0, j = 0; i < a.length && j < b.length; ) {
            if (a[i] == b[j]) {
                result.add(a[i]);
                i++;
                j++;
            } else if (a[i] < b[j]) {
                i++;
            } else {
                j++;
            }
        }
        return result;
    }
}
