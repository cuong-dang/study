package com.cuongd.study.alspe.c2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

;

class PA3 {
    private static int[] runningMedians(int[] a) {
        PriorityQueue<Integer> lower = new PriorityQueue<>(Comparator.reverseOrder());
        PriorityQueue<Integer> upper = new PriorityQueue<>();
        int[] ans = new int[a.length];
        int i = 0;
        for (int n : a) {
            if (lower.isEmpty()) {
                lower.add(n);
            } else if (lower.size() == upper.size()) {
                if (n <= lower.peek() || n <= upper.peek()) {
                    lower.add(n);
                } else { // n > upper.peek()
                    lower.add(upper.remove());
                    upper.add(n);
                }
            } else {
                assert lower.size() == upper.size() + 1;
                if (n <= lower.peek()) {
                    upper.add(lower.remove());
                    lower.add(n);
                } else { // n > lower.peek()
                    upper.add(n);
                }
            }
            ans[i++] = lower.peek();
        }
        return ans;
    }

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("data/pa7.txt"));
        int[] a = new int[10000];
        int i = 0;
        for (String line : lines) {
            a[i++] = Integer.parseInt(line);
        }
        int[] b = runningMedians(a);
        int ans = 0;
        for (int n : b) {
            ans += n;
        }
        System.out.println("Ans: " + (ans % 10000));
    }
}
