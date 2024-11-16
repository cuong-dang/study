package com.cuongd.study.dragon.ch2;

import com.cuongd.study.dragon.util.Matcher;

public class Ex246a {
    public static void parse(String s) {
        Matcher m = new Matcher(s);
        root(m);
        if (m.hasNext())
            throw new RuntimeException("syntax error: unexpected input: " + m.getChar());
    }

    private static void root(Matcher m) {
        if (!m.hasNext()) throw new RuntimeException("syntax error: unexpected EOF");
        switch (m.getChar()) {
            case '+':
                op(m, '+');
                break;
            case '-':
                op(m, '-');
                break;
            case 'a':
                expect(m, 'a');
                break;
            default:
                throw new RuntimeException("syntax error: expect +, -, or a; found: " +
                        m.getChar());
        }
    }

    private static void op(Matcher m, char c) {
        expect(m, c);
        root(m);
        root(m);
    }

    private static void expect(Matcher m, char c) {
        if (!m.match(String.valueOf(c)))
            throw new RuntimeException(String.format("syntax error: expect %c, found: %c",
                    c, m.getChar()));
    }

    public static void main(String[] args) {
        parse("a");
        parse("+aa");
        parse("-aa");
        parse("++aaa");
        parse("+-aaa");
        parse("-++aa-aaa");
    }
}
