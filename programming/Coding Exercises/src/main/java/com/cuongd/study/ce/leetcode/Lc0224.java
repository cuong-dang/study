package com.cuongd.study.ce.leetcode;

class Lc0224 {
  private int i;

  public int calculate(String s) {
    return evalExpr(s.replaceAll("\\s+", ""));
  }

  private int evalExpr(String s) {
    int result = evalTerm(s);
    while (i < s.length()) {
      char op = s.charAt(i++);
      if (op == '+' || op == '-') {
        int term2 = evalTerm(s);
        result = op == '+' ? result + term2 : result - term2;
      } else {
        i--;
        return result;
      }
    }
    return result;
  }

  private int evalTerm(String s) {
    int result = evalPrimitive(s);
    while (i < s.length()) {
      char op = s.charAt(i++);
      if (op == '*' || op == '/') {
        int primitive2 = evalPrimitive(s);
        result = op == '*' ? result * primitive2 : result / primitive2;
      } else {
        i--;
        return result;
      }
    }
    return result;
  }

  private int evalPrimitive(String s) {
    StringBuilder sb = new StringBuilder();
    boolean toggle = false;
    while (i < s.length() && s.charAt(i) == '-') {
      toggle = !toggle;
      i++;
    }
    int result;
    if (s.charAt(i++) == '(') {
      result = evalExpr(s);
      i++; // ')'
    } else {
      i--;
      while (i < s.length() && Character.isDigit(s.charAt(i))) {
        sb.append(s.charAt(i++));
      }
      result = Integer.parseInt(sb.toString());
    }
    return toggle ? -result : result;
  }

  public static void main(String[] args) {
    assert new Lc0224().calculate("0") == 0;
    assert new Lc0224().calculate("1 + 1") == 2;
    assert new Lc0224().calculate("1+2+3") == 6;
    assert new Lc0224().calculate("1-2+3") == 2;
    assert new Lc0224().calculate("1+2-3") == 0;
    assert new Lc0224().calculate("1+2*3") == 7;
    assert new Lc0224().calculate("1+(2+3)*4") == 21;
    assert new Lc0224().calculate("-1") == -1;
    assert new Lc0224().calculate("--1") == 1;
    assert new Lc0224().calculate("-(1+1)") == -2;
  }
}
