package com.cuongd.study.adm.ch1;

class Ex32 {
  private static int divide(int a, int b) {
    if (b == 0) throw new AssertionError("Division by zero");
    boolean neg = (a < 0 && b > 0) || (a > 0 && b < 0);
    if (a < 0) a = -a;
    if (b < 0) b = -b;
    return neg ? -divide(a, b, 1, 0) : divide(a, b, 1, 0);
  }

  private static int divide(int a, int b, int x, int q) {
    if (a < b && x == 1) return q;
    if (a < b) return divide(a, b >> 1, x >> 1, q);
    return divide(a - b, b + b, x + x, q + x);
  }

  public static void main(String[] args) {
    assert divide(1, 2) == 0;
    assert divide(2, 2) == 1;
    assert divide(3, 2) == 1;
    assert divide(4, 2) == 2;
    assert divide(5, 2) == 2;
    assert divide(6, 2) == 3;
    assert divide(7, 2) == 3;
    assert divide(8, 2) == 4;
    assert divide(9, 2) == 4;
    assert divide(10, 2) == 5;

    assert divide(0, 2) == 0;

    assert divide(-1, 3) == 0;
    assert divide(-2, 3) == 0;
    assert divide(-3, 3) == -1;
    assert divide(4, -3) == -1;
    assert divide(5, -3) == -1;
    assert divide(6, -3) == -2;
    assert divide(-7, -3) == 2;
    assert divide(-8, -3) == 2;
    assert divide(-9, -3) == 3;
    assert divide(-10, 3) == -3;
    assert divide(11, -3) == -3;
    assert divide(-12, -3) == 4;
  }
}
