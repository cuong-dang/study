package com.cuongd.study.algs.coursera.wk9;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CircularSuffixArray {
    private final String[] a;
    private final Map<String, Integer> indices;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        a = new String[s.length()];
        indices = new HashMap<>(s.length());
        for (int i = 0; i < s.length(); i++) {
            String suffix = s.substring(i) + s.substring(0, i);
            a[i] = suffix;
            indices.put(suffix, i);
        }
        Arrays.sort(a);
    }

    // length of s
    public int length() {
        return a.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        return indices.get(a[i]);
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