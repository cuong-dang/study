package com.cuongd.study.alspe.c2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PA4 {
    private static int num2SumTargets(long[] a) {
        Map<Long, Boolean> m = new HashMap<>();
        for (int i = 0; i < a.length; i++) {
            if (!m.containsKey(a[i])) {
                m.put(a[i], true);
            } else {
                m.put(a[i], false);
            }
        }
        int ans = 0;
        for (long k = -10000; k <= 10000; k++) {
            if (k % 1000 == 0) {
                System.out.println("Checking: " + k);
            }
            if (twoSum(k, a, m)) {
                ans++;
            }
        }
        return ans;
    }

    private static boolean twoSum(long k, long[] a, Map<Long, Boolean> m) {
        for (int i = 0; i < a.length; i++) {
            long t = k - a[i];
            if (m.containsKey(t) && !m.get(t)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("data/pa8.txt"));
        assert lines.size() == 1000000;
        long[] a = new long[1000000];
        for (int i = 0; i < lines.size(); i++) {
            a[i] = Long.parseLong(lines.get(i));
        }
        System.out.println(num2SumTargets(a));
    }
}
