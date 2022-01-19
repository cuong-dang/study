package com.cuongd.study.algsp1.book.ch2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Ex2425<Key extends Comparable<Key>> extends MaxPQ<Key> {
    @Override
    protected boolean less(int i, int j) {
        return !super.less(i, j);
    }

    private static class Elem implements Comparable<Elem> {
        public final int k;
        public final int a;
        public final int b;

        public Elem(int a, int b) {
            this.a = a;
            this.b = b;
            this.k = (int) (Math.pow(a, 3) + Math.pow(b, 3));
        }

        @Override
        public int compareTo(Elem o) {
            return this.k - o.k;
        }

    }

    private static class Pair {
        public final int a;
        public final int b;

        public Pair(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    private static class Candidates {
        private final Set<Integer> distinctElements;
        private final List<Elem> qualified;

        public Candidates() {
            distinctElements = new HashSet<>();
            qualified = new ArrayList<>();
        }

        public void add(Elem e) {
            if (distinctElements.add(e.a) && distinctElements.add(e.b))
                qualified.add(e);
        }

        public void printQualified() {
            if (qualified.size() > 1) {
                System.out.printf("%d ", qualified.get(0).k);
                for (int i = 0; i < qualified.size(); ++i) {
                    Elem e = qualified.get(i);
                    System.out.printf("(%d, %d)", e.a, e.b);
                    if (i != qualified.size() - 1)
                        System.out.print(" ");
                    else
                        System.out.println();
                }
            }
        }
    }

    public static void main(String[] args) {
        final int N = 1000000;

        Ex2425<Elem> pq = new Ex2425<>();
        for (int i = 0; i <= N; ++i) {
            pq.insert(new Elem(i, 0));
        }

        int currSum = 0;
        Candidates candidates = new Candidates();
        while (!pq.isEmpty()) {
            Elem e = pq.delMax();
            if (e.b < N)
                pq.insert(new Elem(e.a, e.b + 1));
            if (e.k != currSum) {
                candidates.printQualified();
                currSum = e.k;
                candidates = new Candidates();
            }
            candidates.add(e);
        }
    }
}
