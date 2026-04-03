package com.cuongd.study.ce.uva;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

// https://onlinejudge.org/external/1/100.pdf
public class Uva00100 {
  public static void main(String[] args) {
    Map<Integer, Integer> dp = new HashMap<>();
    dp.put(1, 1);
    Scanner sc = new Scanner(System.in);
    while (sc.hasNextInt()) {
      int i = sc.nextInt(), j = sc.nextInt();
      int max = 1;
      for (int k = Math.min(i, j); k <= Math.max(i, j); ++k) {
        max = Math.max(max, length(dp, k));
      }
      System.out.printf("%d %d %d\n", i, j, max);
    }
  }

  private static int length(Map<Integer, Integer> dp, int i) {
    if (dp.containsKey(i)) return dp.get(i);

    int ans = 1;
    Stack<Integer> dpKeys = new Stack<>();
    dpKeys.push(i);
    while (true) {
      i = i % 2 == 0 ? i / 2 : 3 * i + 1;
      if (dp.containsKey(i)) {
        ans += dp.get(i);
        break;
      }
      dpKeys.push(i);
      ++ans;
    }
    int k = dp.get(i);
    while (!dpKeys.isEmpty()) {
      dp.put(dpKeys.pop(), ++k);
    }
    return ans;
  }
}
