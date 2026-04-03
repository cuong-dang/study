package com.cuongd.study.algs.ext;

import java.util.ArrayList;
import java.util.List;

public final class Generics {
    public static <T extends Comparable<? super T>>
        T max(List<? extends T> list, int begin, int end) {
        T maxElem = list.get(begin);
        for (++begin; begin < end; ++begin) {
            if (maxElem.compareTo(list.get(begin)) < 0) {
                maxElem = list.get(begin);
            }
        }
        return maxElem;
    }

    private static class A {
        public int aValue;

        public A(int v) {
            aValue = v;
        }
    }

    private static class B extends A implements Comparable<A> {
        public double bValue;

        public B(int a, double b) {
            super(a);
            bValue = b;
        }

        @Override
        public int compareTo(A o) {
            return this.aValue - o.aValue;
        }

        @Override
        public String toString() {
            return "B(" + aValue + ", " + bValue + ")";
        }
    }

    public static void main(String[] args) {
        List<B> list = new ArrayList<>();
        list.add(new B(3, 0.0));
        list.add(new B(2, 0.0));
        list.add(new B(1, 0.0));
        list.add(new B(0, 0.0));
        System.out.println(max(list, 0, 3));
    }
}
