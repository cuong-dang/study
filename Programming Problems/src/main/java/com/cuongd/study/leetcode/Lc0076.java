package com.cuongd.study.leetcode;

import java.util.HashMap;
import java.util.Map;

public class Lc0076 {
    public String minWindow(String s, String t) {
        // build tCount
        Map<Character, Integer> tCount = new HashMap<>();
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            tCount.put(c, tCount.getOrDefault(c, 0) + 1);
        }
        // process
        int i = 0, j = 0, nDone = 0, minI = 0, minJ = 0;
        while (true) {
            // compact
            while (nDone == tCount.size()) {
                if ((minI == 0 && minJ == 0) || (j - i < minJ - minI)) {
                    minI = i;
                    minJ = j;
                }
                char c = s.charAt(i);
                if (tCount.containsKey(c)) {
                    tCount.put(c, tCount.get(c) + 1);
                    if (tCount.get(c) > 0) {
                        nDone--;
                    }
                }
                i++;
            }
            if (j == s.length()) break;
            // expand
            char c = s.charAt(j);
            if (tCount.containsKey(c)) {
                tCount.put(c, tCount.get(c) - 1);
                if (tCount.get(c) == 0) {
                    nDone++;
                }
            }
            j++;
        }
        if (minJ == 0) return "";
        return s.substring(minI, minJ);
    }

    public static void main(String[] args) {
        System.out.println(new Lc0076().minWindow("ADOBECODEBANC", "ABC"));
    }
}
