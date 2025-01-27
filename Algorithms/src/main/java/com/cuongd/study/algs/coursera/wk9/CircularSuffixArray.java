package com.cuongd.study.algs.coursera.wk9;

import java.util.Arrays;

public class CircularSuffixArray {
    private final Integer[] a;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        a = new Integer[s.length()];
        for (int i = 0; i < s.length(); i++) {
            a[i] = i;
        }
        Arrays.sort(a, (i, j) -> {
            int n = s.length();
            for (int k = 0; k < s.length(); k++) {
                char c1 = s.charAt((i + k) % n), c2 = s.charAt((j + k) % n);
                if (c1 != c2) return c1 - c2;
            }
            return 0;
        });
    }

    // length of s
    public int length() {
        return a.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        return a[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String s = "ABRACADABRA!";
        CircularSuffixArray t = new CircularSuffixArray(s);
        assert t.length() == s.length();
        assert t.index(0) == 11;
        assert t.index(1) == 10;
        assert t.index(2) == 7;
        assert t.index(3) == 0;
        assert t.index(5) == 5;
        assert t.index(6) == 8;
    }
}
