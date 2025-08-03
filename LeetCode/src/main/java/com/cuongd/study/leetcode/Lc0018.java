package com.cuongd.study.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Lc0018 {
    public List<List<Integer>> fourSum(int[] nums, int target) {
        Map<Integer, List<List<Integer>>> twoSum = new HashMap<>();
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                int s = nums[i] + nums[j];
                if (!twoSum.containsKey(s)) {
                    twoSum.put(s, new ArrayList<>());
                }
                twoSum.get(s).add(List.of(i, j));
            }
        }
        List<List<Integer>> withDuplicates = new ArrayList<>(), ans = new ArrayList<>();
        for (Integer s : twoSum.keySet()) {
            if (!twoSum.containsKey(target - s)) continue;
            for (List<Integer> ij : twoSum.get(s)) {
                for (List<Integer> kl : twoSum.get(target - s)) {
                    int i = ij.get(0), j = ij.get(1), k = kl.get(0), l = kl.get(1);
                    if (i == k || i == l || j == k || j == l) continue;
                    withDuplicates.add(List.of(nums[i], nums[j], nums[k], nums[l]));
                }
            }
        }
        // deduplicate
        Set<Map<Integer, Long>> seen = new HashSet<>();
        for (List<Integer> e : withDuplicates) {
            Map<Integer, Long> key = e.stream()
                    .collect(Collectors.groupingBy(n -> n, Collectors.counting()));
            if (!seen.contains(key)) {
                ans.add(e);
                seen.add(key);
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        System.out.println(new Lc0018().fourSum(new int[]{1, 0, -1, 0, -2, 2}, 0));
    }
}
