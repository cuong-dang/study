package com.cuongd.study.algsp1.book.ch1;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Check possible stack permutations */
public class Ex1345 {
    public static boolean isPossiblePermutation(List<Integer> a) {
        MyStack<Integer> next = new MyStack<>();
        Set<Integer> popped = new HashSet<>();
        for (int e : a) {
            if (popped.contains(e)) {
                return false;
            }
            popped.add(e);
            if (next.isEmpty() || (e > (next.peek() + 1))) {
                popped.add(e);
                if (!popped.contains(e - 1)) {
                    next.push(e - 1);
                }
                continue;
            }
            if (e != next.peek()) {
                return false;
            }
            next.pop();
            if (next.isEmpty() ||
                    (next.peek() != (e - 1) && !popped.contains(e - 1))) {
                next.push(e - 1);
            }
        }
        return true;
    }
}
