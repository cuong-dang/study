package com.cuongd.study.algs.coursera.wk9;

public class CircularSuffixArray {
    private static final int R = 256;
    private static final int CUTOFF = 16;
    private final int[] a;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        int n = s.length();
        a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }
        sort(s, a, 0, n - 1, 0, new int[n]);
    }

    private static void sort(String s, int[] a, int lo, int hi, int d, int[] aux) {
        // cutoff to insertion sort for small subarrays
        if (hi <= lo + CUTOFF) {
            insertion(s, a, lo, hi, d);
            return;
        }

        // compute frequency counts
        int[] count = new int[R + 2];
        for (int i = lo; i <= hi; i++) {
            int c = charAt(s, a[i], d);
            count[c + 2]++;
        }

        // transform counts to indices
        for (int r = 0; r < R + 1; r++)
            count[r + 1] += count[r];

        // distribute
        for (int i = lo; i <= hi; i++) {
            int c = charAt(s, a[i], d);
            aux[count[c + 1]++] = a[i];
        }

        // copy back
        for (int i = lo; i <= hi; i++)
            a[i] = aux[i - lo];

        // recursively sort for each character (excludes sentinel -1)
        for (int r = 0; r < R; r++)
            sort(s, a, lo + count[r], lo + count[r + 1] - 1, d + 1, aux);
    }

    private static int charAt(String s, int i, int d) {
        if (d == s.length()) return -1;
        return s.charAt((i + d) % s.length());
    }

    private static void insertion(String s, int[] a, int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(s, a[j], a[j - 1], d); j--)
                exch(a, j, j - 1);
    }

    // exchange a[i] and a[j]
    private static void exch(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    // is v less than w, starting at character d
    private static boolean less(String s, int v, int w, int d) {
        // assert v.substring(0, d).equals(w.substring(0, d));
        for (int i = d; i < s.length(); i++) {
            if (charAt(s, v, i) < charAt(s, w, i)) return true;
            if (charAt(s, v, i) > charAt(s, w, i)) return false;
        }
        return false;
    }

    // length of s
    public int length() {
        return a.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= a.length) {
            throw new IllegalArgumentException();
        }
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
