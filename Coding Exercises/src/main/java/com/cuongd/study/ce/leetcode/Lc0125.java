package com.cuongd.study.ce.leetcode;

public class Lc0125 {
  public boolean isPalindrome(String s) {
    int n = s.length();
    if (n == 0 || n == 1) return true;
    s = s.strip().toLowerCase();
    int i = 0, j = s.length() - 1;
    while (i < j) {
      char c1 = s.charAt(i), c2 = s.charAt(j);
      if (!Character.isLetterOrDigit(c1)) {
        i++;
        continue;
      }
      if (!Character.isLetterOrDigit(c2)) {
        j--;
        continue;
      }
      if (c1 != c2) return false;
      i++;
      j--;
    }
    return true;
  }
}
