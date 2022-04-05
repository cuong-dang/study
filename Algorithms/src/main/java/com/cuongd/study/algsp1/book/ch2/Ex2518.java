package com.cuongd.study.algsp1.book.ch2;

import java.util.Arrays;

public class Ex2518<S extends SortCommon> {
    private final S sorter;

    public Ex2518(S sorter) {
        this.sorter = sorter;
    }

    public void sort(Comparable[] a) {
        StableKey[] stableA = new StableKey[a.length];
        for (int i = 0; i < a.length; ++i)
            stableA[i] = new StableKey(a[i], i);
        this.sorter.sort(stableA);
        for (int i = 0; i < a.length; ++i)
            a[i] = stableA[i].value;
    }

    private static class StableKey<T extends Comparable> implements Comparable {
        private final T value;
        private final int index;

        public StableKey(T value, int index) {
            this.value = value;
            this.index = index;
        }

        @Override
        public int compareTo(Object o) {
            StableKey<T> that = (StableKey<T>) o;
            if (this.value.compareTo(that.value) == 0)
                return this.index - that.index;
            return this.value.compareTo(that.value);
        }
    }

    public static void main(String[] args) {
        TestStable[] a = new TestStable[]{
                new TestStable(1, "a"),
                new TestStable(1, "b"),
                new TestStable(1, "c"),
                new TestStable(0, "d"),
                new TestStable(1, "e"),
                new TestStable(0, "f"),
                new TestStable(0, "g"),
                new TestStable(0, "h"),
                new TestStable(1, "i"),
                new TestStable(0, "j")
        };
        TestStable[] b = Arrays.copyOf(a, a.length);
        SelectionSort sorter = new SelectionSort();
        Ex2518<SelectionSort> stableWrapper = new Ex2518<>(sorter);
        sorter.sort(a);
        stableWrapper.sort(b);
        for (TestStable testStable : a) System.out.println(testStable);
        System.out.println();
        for (TestStable testStable : b) System.out.println(testStable);
    }

    private static class TestStable implements Comparable {
        private final int sortKey;
        private final String testField;

        public TestStable(int sortKey, String testField) {
            this.sortKey = sortKey;
            this.testField = testField;
        }

        @Override
        public int compareTo(Object o) {
            return sortKey - ((TestStable) o).sortKey;
        }

        @Override
        public String toString() {
            return String.format("{%d,%s}", sortKey, testField);
        }
    }
}
