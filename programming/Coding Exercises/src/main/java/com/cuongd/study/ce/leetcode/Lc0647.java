package com.cuongd.study.ce.leetcode;

public class Lc0647 {
  private boolean[][] dp;

  public int countSubstrings(String s) {
    dp = new boolean[s.length()][s.length()];
    int ans = 0;
    for (int sz = 1; sz <= s.length(); sz++) {
      for (int i = 0; i <= s.length() - sz; i++) {
        if (sz == 1) {
          dp[i][i + sz - 1] = true;
        } else if (sz == 2) {
          dp[i][i + sz - 1] = s.charAt(i) == s.charAt(i + sz - 1);
        } else {
          dp[i][i + sz - 1] = s.charAt(i) == s.charAt(i + sz - 1) && dp[i + 1][i + sz - 2];
        }
        ans += dp[i][i + sz - 1] ? 1 : 0;
      }
    }
    return ans;
  }
}
