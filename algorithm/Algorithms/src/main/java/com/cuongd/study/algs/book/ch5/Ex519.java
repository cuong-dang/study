package com.cuongd.study.algs.book.ch5;

import java.util.Arrays;

public class Ex519 {
    private static int R = 256;
    private static String[] aux;

    private static int charAt(String s, int dMinus) {
        int at = (s.length()-1) - dMinus;
        return at >= 0 ? s.charAt(at) : -1;
    }

    public static void sort(String[] a) {
        int N = a.length;
        aux = new String[N];
        sort(a, 0, N-1, 0);
    }

    private static void sort(String[] a, int lo, int hi, int dMinus) {
        if (hi <= lo) return;
        int[] count = new int[R+2];
        for (int i = lo; i <= hi; i++) { // Compute frequency counts.
            count[charAt(a[i], dMinus) + 2]++;
        }
        for (int r = 0; r < R+1; r++) { // Transform counts to indices.
            count[r+1] += count[r];
        }
        for (int i = lo; i <= hi; i++) { // Distribute.
            aux[count[charAt(a[i], dMinus) + 1]++] = a[i];
        }
        for (int i = lo; i <= hi; i++) { // Copy back.
            a[i] = aux[i - lo];
        }
        for (int r = 0; r < R; r++) {
            sort(a, lo + count[r], lo + count[r+1] - 1, dMinus+1);
        }
    }

    public static void main(String[] args) {
        String[] a = new String[]{"now", "is", "the", "time", "for", "all",
                "good", "people", "to", "come", "to", "the", "aid", "of"};
        Ex519.sort(a);
        System.out.println(Arrays.toString(a));
        a = new String[]{"won", "si", "eht", "emit", "rof", "lla",
                "doog", "elpoep", "ot", "emoc", "ot", "eht", "dia", "fo"};
        MSD.sort(a);
        System.out.println(Arrays.toString(a));
        String[] b = new String[]{"she", "sells", "seashells", "by", "the",
                "seashore", "the", "shells", "she", "sells", "are", "surely",
                "seashells"};
        Ex519.sort(b);
        System.out.println(Arrays.toString(b));
    }
}
