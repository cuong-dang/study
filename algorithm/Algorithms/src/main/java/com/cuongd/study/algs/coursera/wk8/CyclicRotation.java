package com.cuongd.study.algs.coursera.wk8;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CyclicRotation {
    public CyclicRotation(String[] a, int L) {
        Map<String, Set<String>> m = new HashMap<>();
        for (String s : a) {
            for (int d = 0; d < L; d++) {
                String rotation = s.substring(d) + s.substring(0, d);
                if (!m.containsKey(rotation)) {
                    m.put(rotation, new HashSet<>());
                }
                m.get(rotation).add(s);
            }
        }
        Set<Set<String>> result = new HashSet<>();
        for (Set<String> rotations : m.values()) {
            if (rotations.size() > 1) {
                result.add(rotations);
            }
        }
        for (Set<String> rotations :result) {
            for (String s : rotations) {
                System.out.printf("%s ", s);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        new CyclicRotation(new String[]{"ab", "ba"}, 2);
        new CyclicRotation(new String[]{"abc", "bca", "xyz"}, 2);
        new CyclicRotation(new String[]{"algorithms", "polynomial",
                "sortsuffix", "boyermoore", "structures", "minimumcut",
                "suffixsort", "stackstack", "binaryheap", "digraphdfs",
                "stringsort", "digraphbfs"
        }, 10);
    }
}
