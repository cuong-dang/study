package com.cuongd.study.algsp1.book.ch2;

public class Ex2219 extends MergeSortTopDown {
    public static int countMergeInversions(Comparable[] a, int lo, int mid, int hi,
                                           Comparable[] aux) {
        int numInversions = 0;
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; ++k)
            aux[k] = a[k];
        for (int k = lo; k <= hi; ++k) {
            if (i > mid) a[k] = aux[j++];
            else if (j > hi) a[k] = aux[i++];
            else if (less(aux[i], aux[j])) a[k] = aux[i++];
            else {
                numInversions += mid + 1 - i;
                a[k] = aux[j++];
            }
        }
        return numInversions;
    }

    public static int countInversions(Comparable[] a) {
        Comparable[] aux = new Comparable[a.length];
        return countInversions(a, 0, a.length - 1, aux);
    }

    private static int  countInversions(Comparable[] a, int lo, int hi, Comparable[] aux) {
        if (lo >= hi) return 0;
        int mid = lo + (hi - lo) / 2;
        int left = countInversions(a, lo, mid, aux);
        int right = countInversions(a, mid+1, hi, aux);
        return left + right + countMergeInversions(a, lo, mid, hi, aux);
    }
}
