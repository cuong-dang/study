package com.cuongd.study.algs.book.ch3;

import org.apache.commons.math3.util.Precision;

/**
 * SparseVector
 */
public class Ex3516 {
    private final LinearProbingST<Integer, Double> st;

    public Ex3516() {
        st = new LinearProbingST<>();
    }

    public int size() {
        return st.n();
    }

    public void put(int i, double x) {
        st.put(i, x);
    }

    public double get(int i) {
        if (!st.contains(i)) return 0.0;
        else return st.get(i);
    }

    public double dot(double[] that) {
        double sum = 0.0;
        for (int i : st.keys())
            sum += that[i] * this.get(i);
        return sum;
    }

    public Ex3516 sum(Ex3516 that) {
        Ex3516 ans = new Ex3516();
        for (int i : st.keys()) {
            if (that.st.contains(i)) {
                double sum = this.get(i) + that.get(i);
                if (!Precision.equals(sum, 0)) {
                    ans.put(i, sum);
                }
            } else {
                ans.put(i, this.get(i));
            }
        }
        for (int j : that.st.keys()) {
            if (!st.contains(j)) {
                ans.put(j, that.get(j));
            }
        }
        return ans;
    }
}
