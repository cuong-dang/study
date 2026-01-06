package com.cuongd.study.alspe.c2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.abs;

class PA4 {
    private static int num2SumTargets(long[] a) {
        Map<Long, Set<Long>> m = new HashMap<>();
        for (long x : a) {
            long key = abs(x / 10000);
            Set<Long> s = m.getOrDefault(key, new HashSet<>());
            s.add(x);
            m.put(key, s);
        }
        int ans = 0;
        Set<Long> seen = new HashSet<>();
        for (long x : a) {
            for (int j = -1; j <= 1; j++) {
                long key = x / 10000 + j;
                if (m.containsKey(key)) {
                    Set<Long> s = m.get(key);
                    for (long y : s) {
                        long t = x + y;
                        if (!seen.contains(t) && -10000 <= t && t <= 10000 && x != y) {
                            seen.add(t);
                            ans += 1;
                        }
                    }
                }
            }
        }
        return ans;
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
