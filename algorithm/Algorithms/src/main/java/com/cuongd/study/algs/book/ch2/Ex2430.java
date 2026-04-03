package com.cuongd.study.algs.book.ch2;

public class Ex2430 {
    private final MinPQ<Double> minPq;
    private final MaxPQ<Double> maxPq;
    private int N;

    public Ex2430() {
        minPq = new MinPQ<>();
        maxPq = new MaxPQ<>();
    }

    public double median() {
        if (N % 2 == 0) {
            return (minPq.peek() + maxPq.peek()) / 2;
        }
        return maxPq.peek();
    }

    public void insert(double value) {
        if (maxPq.size() == 0) {
            maxPq.insert(value);
        } else if (value < median()) {
            if (maxPq.size() > minPq.size()) {
                minPq.insert(maxPq.delMax());
                maxPq.insert(value);
            } else {
                maxPq.insert(value);
            }
        } else {
            if (maxPq.size() == minPq.size()) {
                if (value < minPq.peek()) {
                    maxPq.insert(value);
                } else {
                    maxPq.insert(minPq.delMax());
                    minPq.insert(value);
                }
            } else {
                minPq.insert(value);
            }
        }
        ++N;
    }

    public void delMedian() {
        if (maxPq.size() > minPq.size())
            maxPq.delMax();
        else
            minPq.delMax();
        --N;
    }
}
