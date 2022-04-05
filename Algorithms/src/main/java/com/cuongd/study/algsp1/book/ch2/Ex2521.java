package com.cuongd.study.algsp1.book.ch2;

import java.util.ArrayList;
import java.util.List;

public class Ex2521 implements Comparable<Ex2521> {
    public final List<Integer> v;

    public Ex2521(int... a) {
        v = new ArrayList<>();
        for (int e : a)
            v.add(e);
    }

    @Override
    public int compareTo(Ex2521 that) {
        assert this.v.size() == that.v.size();
        for (int i = 0; i < this.v.size(); ++i) {
            int thisElem = this.v.get(i), thatElem = that.v.get(i);
            if (thisElem != thatElem)
                return thisElem - thatElem;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Ex2521{" +
                "v=" + v +
                '}';
    }

    public static void main(String[] args) {
        Ex2521[] a = new Ex2521[]{new Ex2521(1, 1), new Ex2521(1, 0), new Ex2521(0, 1)};
        QuickSort sorter = new QuickSort();
        sorter.sort(a);
        for (Ex2521 aa : a)
            System.out.println(aa);
    }
}
