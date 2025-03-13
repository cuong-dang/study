package com.cuongd.study.leetcode;

import java.util.ArrayList;
import java.util.List;

public class Lc0282 {
    private String n;
    private int target;
    private List<String> ans;

    public List<String> addOperators(String num, int target) {
        this.n = num;
        this.target = target;
        ans = new ArrayList<>();
        run(0, 0, 0, 0, new ArrayList<>());
        return ans;
    }

    private void run(int i, long value, long undo, long operand, List<String> collector) {
        if (i == n.length()) {
            if (value == target && operand == 0) {
                StringBuilder sb = new StringBuilder();
                collector.subList(1, collector.size()).forEach(sb::append);
                ans.add(sb.toString());
            }
            return;
        }
        operand = operand * 10 + Character.getNumericValue(n.charAt(i));
        String opStr = Long.toString(operand);
        if (operand > 0) {
            run(i + 1, value, undo, operand, collector);
        }
        // try add
        collector.add("+");
        collector.add(opStr);
        run(i + 1, value + operand, operand, 0, collector);
        collector.remove(collector.size() - 1);
        collector.remove(collector.size() - 1);
        if (!collector.isEmpty()) {
            // try minus
            collector.add("-");
            collector.add(opStr);
            run(i + 1, value - operand, -operand, 0, collector);
            collector.remove(collector.size() - 1);
            collector.remove(collector.size() - 1);
            // try multiply
            collector.add("*");
            collector.add(opStr);
            run(i + 1, (value - undo) + undo * operand, undo * operand, 0, collector);
            collector.remove(collector.size() - 1);
            collector.remove(collector.size() - 1);
        }
    }

    public static void main(String[] args) {
        System.out.println(new Lc0282().addOperators("123", 6));
        System.out.println(new Lc0282().addOperators("232", 8));
        System.out.println(new Lc0282().addOperators("105", 5));
        System.out.println(new Lc0282().addOperators("123456789", 45));
    }
}
