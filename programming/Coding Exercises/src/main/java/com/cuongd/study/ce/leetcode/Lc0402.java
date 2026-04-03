package com.cuongd.study.ce.leetcode;

import java.util.*;

// Remove K Digits
public class Lc0402 {
  public String removeKdigits(String num, int k) {
    if (k == 0) return num;
    if (k == num.length()) return "0";
    Stack<Integer> s = new Stack<>();
    for (int i = 0; i < num.length(); ++i) {
      int x = Integer.parseInt(Character.toString(num.charAt(i)));
      while (k > 0 && !s.isEmpty() && x < s.peek()) {
        s.pop();
        --k;
      }
      s.push(x);
    }
    StringBuilder sb = new StringBuilder();
    while (k-- > 0) s.pop();
    while (!s.isEmpty()) {
      sb.insert(0, s.pop());
    }
    while (sb.length() > 0 && sb.charAt(0) == '0') {
      sb.deleteCharAt(0);
    }
    return sb.length() == 0 ? "0" : sb.toString();
  }

  public static void main(String[] args) {
    System.out.println(new Lc0402().removeKdigits("1203", 3));
  }
}
