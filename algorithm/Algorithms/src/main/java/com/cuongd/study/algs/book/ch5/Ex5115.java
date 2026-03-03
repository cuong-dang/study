package com.cuongd.study.algs.book.ch5;

import java.util.Arrays;

public class Ex5115 {
    private static int digitAt(int n, int d) {
        return (n << (31 - d)) >>> 31;
    }

    public static void sort(int[] a) {
        int N = a.length;
        int R = 10;
        int[] aux = new int[N];
        for (int d = 16; d < 32; d++) {
            int[] count = new int[10+1];
            for (int i = 0; i < N; i++) {
                count[digitAt(a[i], d) + 1]++;
            }
            for (int r = 0; r < R; r++) {
                count[r+1] += count[r];
            }
            for (int i = 0; i < N; i++) {
                aux[count[digitAt(a[i], d)]++] = a[i];
            }
            for (int i = 0; i < N; i++) {
                a[i] = aux[i];
            }
        }
        for (int i = 0; i < N; i++) {
            for (int j = i; j > 0 && a[j] < a[j-1]; j--)
                exch(a, j, j-1);
        }
    }

    private static void exch(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    public static void main(String[] args) {
        int[] a = new int[]{65_536_321, 6_553_621, 655_361, 655_364_321,
                553_654_321, 367_654_321, 987_654_321, 687_654_321,
                536_654_321};
        sort(a);
        System.out.println(Arrays.toString(a));
    }
}
