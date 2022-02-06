package com.cuongd.study.algsp1.book.ch2;

public class Ex2431<Key extends Comparable<Key>> extends MaxPQ<Key> {
    @Override
    protected void swim(int k) {
        swimBinary(k, 1, k);
    }

    private void swimBinary(int k, int lo, int hi) {
        if (lo == hi)
            if (less(lo, k)) {
                while (k != lo) {
                    exch(k, k / 2);
                    k = k/2;
                }
                return;
            }
        else
            return;
        int n = (int) (Math.log(k) / Math.log(2));
        int mid = (int) (k / Math.pow(2, Math.floor((double) n / 2 + 1)));
        if (less(mid, k))
            swimBinary(k, lo, mid);
        else
            swimBinary(k, mid, hi / 2);
    }
}
