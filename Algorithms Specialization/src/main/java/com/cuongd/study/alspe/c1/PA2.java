package com.cuongd.study.alspe.c1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

class PA2 {
    static long solve() throws FileNotFoundException, IOException {
        int[] a = new int[100_000];
        // read
        try (Scanner s = new Scanner(new BufferedReader(new FileReader("data/pa2.txt")))) {
            int i = 0;
            while (s.hasNext()) {
                a[i++] = Integer.parseInt(s.next());
            }
        }
        int[] aux = new int[a.length];
        return count(a, 0, a.length - 1, aux);
    }

    static long count(int[] a, int lo, int hi, int[] aux) {
        if (lo == hi) return 0;
        int mid = (lo + hi) / 2;
        long left = count(a, lo, mid, aux);
        long right = count(a, mid + 1, hi, aux);
        return left + right + countSplit(a, lo, hi, mid, aux);
    }

    static long countSplit(int[] a, int lo, int hi, int mid, int[] aux) {
        int i = lo, j = mid + 1;
        long count = 0;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                aux[k] = a[j++];
            } else if (j > hi) {
                aux[k] = a[i++];
            } else if (a[i] < a[j]) {
                aux[k] = a[i++];
            } else {
                count += mid - i + 1;
                aux[k] = a[j++];
            }
        }
        System.arraycopy(aux, lo, a, lo, hi - lo + 1);
        return count;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(solve());
    }
}
