package com.cuongd.study.algsp1.book.ch1;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Ex139 {
    public static void main(String[] args) {
        StdOut.println(insertParentheses(StdIn.readAll()));
    }

    public static String insertParentheses(String s) {
        /* Assume well-formed s. */
        s = s.replaceAll("\\s+", "");
        MyStack<String> stack = new MyStack<>();
        /* Insert parentheses. */
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ')') {
                if (stack.size() == 1) {
                    String expression = stack.pop();
                    stack.push(String.format("(%s)", expression));
                } else {
                    String rightOperand = stack.pop();
                    String operator = stack.pop();
                    String leftOperand = stack.pop();
                    String expression = String.format("(%s %s %s)",
                            leftOperand, operator, rightOperand);
                    stack.push(expression);
                }
            } else {
                stack.push(Character.toString(c));
            }
        }
        /* Combine stack elements into result string. */
        if (stack.size() == 1)
            return stack.pop();
        StringBuilder sb = new StringBuilder();
        for (String e : stack) {
            sb.append(e);
            sb.append(" ");
        }
        return sb.reverse().toString().strip();
    }
}
