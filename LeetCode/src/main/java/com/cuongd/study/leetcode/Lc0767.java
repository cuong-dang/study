package com.cuongd.study.leetcode;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

class Lc0767 {
    private int[] count;

    public String reorganizeString(String s) {
        count = new int[26];
        Arrays.fill(count, 0);
        doCount(s);
        Comparator<Integer> cmp = new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return Integer.compare(count[a], count[b]);
            }
        }.reversed();
        PriorityQueue<Integer> pq = new PriorityQueue<>(cmp);
        for (int i = 0; i < count.length; i++) {
            if (count[i] != 0) {
                pq.add(i);
            }
        }
        if (count[pq.peek()] > Math.ceil((double) s.length() / 2)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            int i1 = pq.remove();
            char c1 = (char) ('a' + i1);
            if (sb.length() == 0 || c1 != sb.charAt(sb.length() - 1)) {
                sb.append(c1);
                count[i1]--;
                pq.add(i1);
            } else if (pq.isEmpty()) {
                return "";
            } else {
                int i2 = pq.remove();
                char c2 = (char) ('a' + i2);
                sb.append(c2);
                count[i2]--;
                pq.add(i2);
            }
            i++;
        }
        return sb.toString();
    }

    private void doCount(String s) {
        for (int i = 0; i < s.length(); i++) {
            count[s.charAt(i) - 'a']++;
        }
    }

    public static void main(String[] args) {
        assert new Lc0767().reorganizeString("aab").equals("aba");
        assert new Lc0767().reorganizeString("aaab").equals("");
    }

}
