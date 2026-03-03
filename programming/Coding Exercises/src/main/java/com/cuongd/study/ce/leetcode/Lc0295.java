package com.cuongd.study.ce.leetcode;

import java.util.PriorityQueue;

class Lc0295 {
  private static class MedianFinder {
    private PriorityQueue<Double> minPq;
    private PriorityQueue<Double> maxPq;

    public MedianFinder() {
      minPq = new PriorityQueue<>();
      maxPq = new PriorityQueue<>((d1, d2) -> -Double.compare(d1, d2));
    }

    public void addNum(int num) {
      double n = num;
      if (maxPq.isEmpty()) {
        maxPq.add(n);
      } else if (less(n, findMedian())) {
        if (!isBalanced()) {
          minPq.add(maxPq.remove());
        }
        maxPq.add(n);
      } else {
        if (!isBalanced()) {
          minPq.add(n);
        } else {
          if (less(n, minPq.peek())) {
            maxPq.add(n);
          } else {
            maxPq.add(minPq.remove());
            minPq.add(n);
          }
        }
      }
    }

    private boolean less(Double x, Double y) {
      return Double.compare(x, y) < 0;
    }

    private boolean isBalanced() {
      return minPq.size() == maxPq.size();
    }

    public double findMedian() {
      if (minPq.size() == maxPq.size()) {
        return (minPq.peek() + maxPq.peek()) / 2;
      }
      return maxPq.peek();
    }
  }
}
