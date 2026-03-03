package com.cuongd.study.ce.hackerrank;

public class PowerSum {
  public static int powerSum(int X, int N) {
    return powerSum(X, N, 1);
  }

  private static int powerSum(int X, int N, int K) {
    if (X == 0) return 1;
    int KN = (int) Math.pow(K, N);
    if (X - KN < 0) return 0;
    return powerSum(X - KN, N, K + 1) + powerSum(X, N, K + 1);
  }

  public static void main(String[] args) {
    assert powerSum(5, 2) == 1;
    assert powerSum(10, 2) == 1;
    assert powerSum(100, 2) == 3;
  }
}
