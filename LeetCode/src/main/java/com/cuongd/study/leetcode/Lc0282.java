package com.cuongd.study.leetcode;

import java.util.ArrayList;
import java.util.List;

public class Lc0282 {
    private int target;
    private List<String> ans;

    public List<String> addOperators(String num, int target) {
        this.target = target;
        ans = new ArrayList<>();
        run(num, 0, 0, 0, 0, new StringBuilder());
        return ans;
    }

    private void run(String s, int i, long currVal, long undo, long t, StringBuilder sb) {
        if (i == s.length()) {
            if (currVal == target) {
                ans.add(sb.toString());
            }
            return;
        }
        long val = t * 10 + Character.getNumericValue(s.charAt(i));
        String valS = Long.toString(val);
        if (i == 0) {
            sb.append(s.charAt(0));
            run(s, 1, val, val, 0, sb);
            return;
        }
//        if (val > 0) {
//            run(s, i + 1, currVal, undo, val, sb);
//        }
        // try add
        sb.append("+");
        sb.append(valS);
        run(s, i + 1, currVal + val, val, 0, sb);
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        // try minus
        sb.append("-");
        sb.append(valS);
        run(s, i + 1, currVal - val, -val, 0, sb);
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        // try multiply
        sb.append("*");
        sb.append(valS);
        run(s, i + 1, (currVal - undo) + undo * val, undo * val, 0, sb);
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
    }

    public static void main(String[] args) {
        System.out.println(new Lc0282().addOperators("123", 6));
        System.out.println(new Lc0282().addOperators("232", 8));
        System.out.println(new Lc0282().addOperators("105", 5));
        System.out.println(new Lc0282().addOperators("3456237490", 9191));
    }
}
