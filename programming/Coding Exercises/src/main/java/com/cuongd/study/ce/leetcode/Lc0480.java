package com.cuongd.study.ce.leetcode;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;

class Lc0480 {
  PriorityQueue<Integer> min = new PriorityQueue<>(Collections.reverseOrder());
  PriorityQueue<Integer> max = new PriorityQueue<>();
  HashMap<Integer, Integer> map = new HashMap<>();

  public double[] medianSlidingWindow(int[] nums, int k) {
    double[] medians = new double[nums.length - k + 1];
    int i = 0;
    while (i < k) {
      min.add(nums[i++]);
    }
    for (int j = 0; j < k / 2; j++) {
      max.add(min.poll());
    }

    int itr = 0;
    while (true) {
      medians[itr++] = findMedian(k);
      if (i >= nums.length) break;

      int out = nums[i - k];
      int in = nums[i++];
      int balance = 0;

      balance += out <= min.peek() ? -1 : 1;
      map.put(out, map.getOrDefault(out, 0) + 1);

      if (!min.isEmpty() && in <= min.peek()) {
        balance++;
        min.add(in);
      } else {
        balance--;
        max.add(in);
      }

      if (balance < 0) {
        min.add(max.poll());
        balance++;
      }

      if (balance > 0) {
        max.add(min.poll());
        balance--;
      }

      while (!min.isEmpty() && map.containsKey(min.peek())) {
        map.put(min.peek(), map.get(min.peek()) - 1);
        if (map.get(min.peek()) == 0) {
          map.remove(min.peek());
        }
        min.poll();
      }

      while (!max.isEmpty() && map.containsKey(max.peek())) {
        map.put(max.peek(), map.get(max.peek()) - 1);
        if (map.get(max.peek()) == 0) {
          map.remove(max.peek());
        }
        max.poll();
      }
    }
    return medians;
  }

  private double findMedian(int k) {
    if ((k & 1) == 1) return min.peek();
    return ((double) max.peek() + min.peek()) / 2.0;
  }

  public static void main(String[] args) {
    System.out.println(
        Arrays.toString(new Lc0480().medianSlidingWindow(new int[] {1, 3, -1, -3, 5, 3, 6, 7}, 3)));
  }
}
