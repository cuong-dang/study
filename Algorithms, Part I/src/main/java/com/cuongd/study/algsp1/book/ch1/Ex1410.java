package com.cuongd.study.algsp1.book.ch1;

/** Binary search returning smallest matching index */
public class Ex1410 {
    public static int binSearch(int[] a, int item) {
        return binSearchIter(a, item, 0, a.length - 1);
    }

    private static int binSearchIter(int[] a, int item, int low, int high) {
        if (low > high) {
            return -1;
        }
        int mid = (low + high) / 2;
        if (a[mid] == item) {
            int lowerIndex = binSearchIter(a, item, low, mid - 1);
            if (lowerIndex == -1) {
                return mid;
            } else {
                return lowerIndex;
            }
        } else if (a[mid] < item) {
            return binSearchIter(a, item, mid + 1, high);
        } else {
            return binSearchIter(a, item, low, mid - 1);
        }
    }
}
