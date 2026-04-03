package com.cuongd.study.algs.book.ch2;

public class Ex243<Key extends Comparable<Key>> {
    private Key[] unorderedArray;
    private Key[] orderedArray;
    private final int dataType;
    private int n;

    private static final int N = 64;

    public Ex243(int dataType) {
        this.dataType = dataType;
        n = 0;
        switch (dataType) {
            case 1:
                unorderedArray = (Key[]) new Comparable[N];
                break;
            case 2:
                orderedArray = (Key[]) new Comparable[N];
                break;
            case 3:
            case 4:
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void insert(Key key) {
        switch (dataType) {
            case 1:
                unorderedArrayInsert(key);
                break;
            case 2:
                orderedArrayInsert(key);
        }
    }

    private void unorderedArrayInsert(Key key) {
        unorderedArray[n++] = key;
    }

    private void orderedArrayInsert(Key key) {
        int insertIndex;
        for (insertIndex = 0; insertIndex < n; insertIndex++)
            if (orderedArray[insertIndex].compareTo(key) > 0)
                break;
        for (int i = n; i > insertIndex; i--)
            orderedArray[i] = orderedArray[i-1];
        orderedArray[insertIndex] = key;
        ++n;
    }

    public Key removeMax() {
        switch (dataType) {
            case 1:
                return unorderedArrayRemove();
            case 2:
                return orderedArrayRemove();
            default:
                throw new RuntimeException();
        }
    }

    private Key unorderedArrayRemove() {
        int max = 0;
        for (int i = 1; i < n; ++i)
            if (unorderedArray[i] != null && unorderedArray[max].compareTo(unorderedArray[i]) < 0)
                max = i;
        Key ret = unorderedArray[max];
        unorderedArray[max] = unorderedArray[--n];
        return ret;
    }

    private Key orderedArrayRemove() {
        return orderedArray[--n];
    }
}
