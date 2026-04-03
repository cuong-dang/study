package com.cuongd.study.algs.book.ch1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Ex1310 {
    private static final Set<Character> LOWER_OPS =
            new HashSet<>(Arrays.asList('+', '-'));
    private static final Set<Character> HIGHER_OPS =
            new HashSet<>(Arrays.asList('*', '/'));

    /**
     * Convert an arithmetic expression from infix to postfix. Support
     * operators +, -, *, /, and parentheses.
     *
     * @param s infix expression string. Assume well-formed.
     * @return postfix expression string.
     */
    public static String infixToPostfix(String s) {
        s = s.replaceAll("\\s+", "");
        MyStack<String> terms = new MyStack<>();
        MyStack<Character> ops = new MyStack<>();
        /* Accumulate terms and ops. Recurse on expressions in parens. */
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '(') {
                int j = findClosingParenIndex(s.substring(i));
                terms.push(infixToPostfix(s.substring(i + 1, i+j)));
                i += j + 1;
            } else if (LOWER_OPS.contains(c) || HIGHER_OPS.contains(c)) {
                ops.push(c);
                i++;
            } else {
                terms.push(Character.toString(c));
                i++;
            }
        }
        /* Process higher precedence terms. */
        MyStack<Character> lowerOps = new MyStack<>();
        MyStack<String> lowerTerms = new MyStack<>();
        terms.reverse();
        ops.reverse();
        while (!ops.isEmpty()) {
            char op = ops.pop();
            if (HIGHER_OPS.contains(op)) {
                String leftTerm = terms.pop();
                String rightTerm = terms.pop();
                terms.push(String.format("%s %s %s",
                        leftTerm, rightTerm, op));
            } else {
                lowerOps.push(op);
                lowerTerms.push(terms.pop());
            }
        }
        while (!terms.isEmpty()) {
            lowerTerms.push(terms.pop());
        }
        lowerTerms.reverse();
        lowerOps.reverse();
        /* Process lower precedence terms. */
        while (!lowerOps.isEmpty()) {
            String leftTerm = lowerTerms.pop();
            String rightTerm = lowerTerms.pop();
            lowerTerms.push(String.format("%s %s %s",
                    leftTerm, rightTerm, lowerOps.pop()));
        }
        assert (lowerTerms.size() == 1);
        return lowerTerms.pop();
    }

    public static int findClosingParenIndex(String s) {
        MyStack<Integer> stack = new MyStack<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ')') {
                stack.pop();
                if (stack.isEmpty()) {
                    return i;
                }
            } else if (c == '(') {
                stack.push(0);
            }
        }
        throw new IllegalArgumentException();
    }
}
