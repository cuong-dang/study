package com.cuongd.study.algs.book.ch5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ex5114 {
    public static void sort(int[][] a) {
        int N = a.length;
        String[] aux = new String[N];
        int W = 0;
        for (int[] is : a) {
            if (W < is.length) {
                W = is.length;
            }
        }
        for (int i = 0; i < N; i++) {
            StringBuilder sb = new StringBuilder();
            if (a[i].length < W) {
                sb.append("0".repeat(Math.max(0, W - a[i].length)));
            }
            for (int d : a[i]) {
                sb.append(d);
            }
            aux[i] = sb.toString();
        }
        Quick3string.sort(aux);
        for (int i = 0; i < N; i++) {
            List<Integer> is = new ArrayList<>();
            boolean leadingZeroes = true;
            for (int j = 0; j < aux[i].length(); j++) {
                int d = Integer.parseInt(String.format("%c", aux[i].charAt(j)));
                if (leadingZeroes && d == 0) continue;
                if (leadingZeroes) {
                    leadingZeroes = false;
                }
                is.add(d);
            }
            a[i] = new int[is.size()];
            for (int j = 0; j < a[i].length; j++) {
                a[i][j] = is.get(j);
            }
        }
    }

    public static void main(String[] args) {
        int[][] a = new int[][]{{1, 2}, {1, 2, 3}, {1, 1, 2}, {1}, {1, 0}};
        sort(a);
        System.out.println(Arrays.deepToString(a));
    }
}
