package com.cuongd.study.alspe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Heap<T> {
    private final List<T> a;
    private int n;
    private final Comparator<T> cmp;
    private final Map<T, Integer> indexOf;

    public Heap(Comparator<T> cmp) {
        a = new ArrayList<>();
        a.add(null); // skip 0 index
        this.n = 0;
        this.cmp = cmp;
        this.indexOf = new HashMap<>();
    }

    public int size() {
        return n;
    }

    public boolean isEmpty() {
        return n == 0;
    }

    public void insert(T x) {
        if (a.size() - 1 == n) {
            a.add(x);
        } else {
            a.set(n + 1, x);
        }
        indexOf.put(x, n + 1);
        swim(n + 1);
        n++;
    }

    public T remove() {
        T result = a.get(1);
        indexOf.remove(result);
        a.set(1, a.get(n));
        indexOf.put(a.get(n), 1);
        a.set(n, null);
        n--;
        sink(1);
        return result;
    }

    public boolean remove(T x) {
        if (!indexOf.containsKey(x)) {
            return false;
        }
        int xi = indexOf.get(x);
        a.set(xi, a.get(n));
        indexOf.put(a.get(n), xi);
        a.set(n, null);
        indexOf.remove(x);
        n--;
        if (xi == n + 1) {
            return true;
        }
        swim(xi);
        sink(xi);
        return true;
    }

    private void swim(int i) {
        while (i > 1) {
            int parent = i / 2;
            if (cmp.compare(a.get(i), a.get(parent)) < 0) {
                swap(i, parent);
                i = parent;
            } else {
                break;
            }
        }
    }

    private void sink(int i) {
        if (2 * i > n) {
            return;
        }
        while (true) {
            T x = a.get(i), child1 = a.get(2 * i);
            T child2 = 2 * i + 1 > n ? child1 : a.get(2 * i + 1);
            if (cmp.compare(x, child1) <= 0 && cmp.compare(x, child2) <= 0) {
                break;
            }
            int j = cmp.compare(child1, child2) <= 0 ? 2 * i : 2 * i + 1;
            swap(i, j);
            sink(j);
        }
    }

    private void swap(int i, int j) {
        T t = a.get(i), u = a.get(j);
        a.set(i, a.get(j));
        a.set(j, t);
        indexOf.put(t, j);
        indexOf.put(u, i);
    }

    public static void main(String[] args) {
        Heap<Integer> h = new Heap<>((a, b) -> Integer.compare(a, b));
        h.insert(2);
        h.insert(0);
        h.insert(1);
        assert h.size() == 3;
        assert h.a.get(1) == 0;
        assert h.a.get(2) == 2;
        assert h.a.get(3) == 1;
        assert h.indexOf.get(0) == 1;
        assert h.indexOf.get(2) == 2;
        assert h.indexOf.get(1) == 3;

        assert h.remove() == 0;
        assert h.size() == 2;
        assert h.a.get(1) == 1;
        assert h.a.get(2) == 2;
        assert h.indexOf.get(1) == 1;
        assert h.indexOf.get(2) == 2;

        assert h.remove() == 1;
        assert h.size() == 1;
        assert h.a.get(1) == 2;
        assert h.indexOf.get(2) == 1;

        assert h.remove() == 2;
        assert h.size() == 0;

        h.insert(4);
        h.insert(3);
        h.insert(6);
        h.insert(5);
        h.insert(2);
        h.insert(7);
        h.insert(1);
        assert h.size() == 7;
        assert h.a.get(1) == 1;
        assert h.a.get(2) == 3;
        assert h.a.get(3) == 2;
        assert h.a.get(4) == 5;
        assert h.a.get(5) == 4;
        assert h.a.get(6) == 7;
        assert h.a.get(7) == 6;

        assert h.remove() == 1;
        assert h.size() == 6;
        assert h.a.get(1) == 2;
        assert h.a.get(2) == 3;
        assert h.a.get(3) == 6;
        assert h.a.get(4) == 5;
        assert h.a.get(5) == 4;
        assert h.a.get(6) == 7;

        assert h.remove() == 2;
        assert h.size() == 5;
        assert h.a.get(1) == 3;
        assert h.a.get(2) == 4;
        assert h.a.get(3) == 6;
        assert h.a.get(4) == 5;
        assert h.a.get(5) == 7;

        assert h.remove() == 3;
        assert h.size() == 4;
        assert h.a.get(1) == 4;
        assert h.a.get(2) == 5;
        assert h.a.get(3) == 6;
        assert h.a.get(4) == 7;
        assert h.indexOf.get(4) == 1;
        assert h.indexOf.get(5) == 2;
        assert h.indexOf.get(6) == 3;
        assert h.indexOf.get(7) == 4;

        h.insert(3);
        h.insert(1);
        assert h.size() == 6;
        assert h.a.get(1) == 1;
        assert h.a.get(2) == 4;
        assert h.a.get(3) == 3;
        assert h.a.get(4) == 7;
        assert h.a.get(5) == 5;
        assert h.a.get(6) == 6;

        h.insert(2);
        assert h.size() == 7;
        assert h.a.get(1) == 1;
        assert h.a.get(2) == 4;
        assert h.a.get(3) == 2;
        assert h.a.get(4) == 7;
        assert h.a.get(5) == 5;
        assert h.a.get(6) == 6;
        assert h.a.get(7) == 3;
        assert h.indexOf.get(1) == 1;
        assert h.indexOf.get(4) == 2;
        assert h.indexOf.get(2) == 3;
        assert h.indexOf.get(7) == 4;
        assert h.indexOf.get(5) == 5;
        assert h.indexOf.get(6) == 6;
        assert h.indexOf.get(3) == 7;

        assert h.remove(3);
        assert !h.remove(3);
        assert h.size() == 6;
        assert h.a.get(1) == 1;
        assert h.a.get(2) == 4;
        assert h.a.get(3) == 2;
        assert h.a.get(4) == 7;
        assert h.a.get(5) == 5;
        assert h.a.get(6) == 6;
        assert h.indexOf.get(1) == 1;
        assert h.indexOf.get(4) == 2;
        assert h.indexOf.get(2) == 3;
        assert h.indexOf.get(7) == 4;
        assert h.indexOf.get(5) == 5;
        assert h.indexOf.get(6) == 6;

        assert h.remove(1);
        assert h.size() == 5;
        assert h.a.get(1) == 2;
        assert h.a.get(2) == 4;
        assert h.a.get(3) == 6;
        assert h.a.get(4) == 7;
        assert h.a.get(5) == 5;
        assert h.indexOf.get(2) == 1;
        assert h.indexOf.get(4) == 2;
        assert h.indexOf.get(6) == 3;
        assert h.indexOf.get(7) == 4;
        assert h.indexOf.get(5) == 5;

        assert h.remove(4);
        assert h.size() == 4;
        assert h.a.get(1) == 2;
        assert h.a.get(2) == 5;
        assert h.a.get(3) == 6;
        assert h.a.get(4) == 7;
        assert h.indexOf.get(2) == 1;
        assert h.indexOf.get(5) == 2;
        assert h.indexOf.get(6) == 3;
        assert h.indexOf.get(7) == 4;
    }
}
