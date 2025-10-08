package com.cuongd.study.ce.leetcode;

import java.util.List;
import java.util.Stack;

public class Lc0739 {
  public int[] dailyTemperatures(int[] temperatures) {
    int[] res = new int[temperatures.length];
    Stack<List<Integer>> s = new Stack<>();
    for (int i = 0; i < temperatures.length; ++i) {
      int t = temperatures[i];
      while (!s.isEmpty() && s.peek().get(0) < t) {
        List<Integer> e = s.pop();
        res[e.get(1)] = i - e.get(1);
      }
      s.push(List.of(t, i));
    }
    while (!s.isEmpty()) {
      res[s.pop().get(1)] = 0;
    }
    return res;
  }
}
