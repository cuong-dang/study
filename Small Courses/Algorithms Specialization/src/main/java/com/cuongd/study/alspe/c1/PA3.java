package com.cuongd.study.alspe.c1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import static com.cuongd.study.alspe.Partition.partition;

class PA3 {
    static long solve() throws FileNotFoundException, IOException {
        int[] a = new int[10_000];
        // read
        try (Scanner s = new Scanner(new BufferedReader(new FileReader("data/pa3.txt")))) {
            int i = 0;
            while (s.hasNext()) {
                a[i++] = Integer.parseInt(s.next());
            }
        }
        return sort(a);
    }

    static long sort(int[] a) {
        return sort(a, 0, a.length - 1);
    }

    static long sort(int[] a, int lo, int hi) {
        if (lo >= hi) return 0;
        int j = partition(a, lo, hi, choosePivot(a, lo, hi));
        long count = hi - lo;
        count += sort(a, lo, j - 1);
        count += sort(a, j + 1, hi);
        return count;
    }

    static int choosePivot(int[] a, int lo, int hi) {
        int me = (lo + hi) / 2;
        if (a[lo] <= a[me] && a[me] <= a[hi]) return me;
        if (a[hi] <= a[me] && a[me] <= a[lo]) return me;

        if (a[me] <= a[hi] && a[hi] <= a[lo]) return hi;
        if (a[lo] <= a[hi] && a[hi] <= a[me]) return hi;

        if (a[me] <= a[lo] && a[lo] <= a[hi]) return lo;
        if (a[hi] <= a[lo] && a[lo] <= a[me]) return lo;

        throw new AssertionError();
    }

    public static void main(String[] args) throws IOException {
        System.out.println(solve());
    }
}
