package com.cuongd.study.algsp1.book.ch1;

/** Bitonic search */
public class Ex1420 {
    public static boolean bitonicContains(int[] a, int key) {
        int sep = findBitonicSeparator(a);

        return incBitonicSearch(a, sep, key) || decBitonicSearch(a, sep, key);
    }

    public static int findBitonicSeparator(int[] a) {
        int lo = 0, hi = a.length - 1, mid = (lo + hi) / 2;
        while (true) {
            if (a[mid-1] < a[mid] && a[mid] > a[mid+1]) {
                return mid;
            }
            if (a[mid-1] < a[mid]) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
            mid = (lo + hi) / 2;
        }
    }

    private static boolean incBitonicSearch(int[] a, int sep, int key) {
        return bitonicSearch(a, 0, sep - 1, key, (x, y) -> x < y);
    }

    private static boolean decBitonicSearch(int[] a, int sep, int key) {
        return bitonicSearch(a, sep, a.length - 1, key, (x, y) -> x > y);
    }

    private static boolean bitonicSearch(int[] a, int loStart, int hiStart, int key,
                                         BitonicComparable cmp) {
        for (int lo = loStart, hi = hiStart, mid = (lo + hi) / 2; lo <= hi; ) {
            if (a[mid] == key) {
                return true;
            } else if (cmp.compare(a[mid], key)) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
            mid = (lo + hi) / 2;
        }
        return false;
    }

    private interface BitonicComparable {
        boolean compare(int x, int y);
    }
}
