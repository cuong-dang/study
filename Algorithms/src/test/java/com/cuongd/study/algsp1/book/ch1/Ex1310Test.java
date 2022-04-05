package com.cuongd.study.algsp1.book.ch1;

import org.junit.Test;

import static com.cuongd.study.algsp1.book.ch1.Ex1310.findClosingParenIndex;
import static com.cuongd.study.algsp1.book.ch1.Ex1310.infixToPostfix;
import static org.junit.Assert.assertEquals;

public class Ex1310Test {
    @Test
    public void testFindClosingParenIndex() {
        assertEquals(1, findClosingParenIndex("()"));
        assertEquals(3 ,findClosingParenIndex("(())"));
    }

    @Test
    public void testInfixToPostfix() {
        assertEquals("1 2 +", infixToPostfix("1 + 2"));
        assertEquals("1 2 + 3 +", infixToPostfix("1 + 2 + 3"));
        assertEquals("1 2 * 3 +", infixToPostfix("1 * 2 + 3"));
        assertEquals("1 2 3 * +", infixToPostfix("1 + 2 * 3"));

        assertEquals("1 2 + 3 + 4 +",
                infixToPostfix("1 + 2 + 3 + 4"));
        assertEquals("1 2 * 3 4 * +",
                infixToPostfix("1 * 2 + 3 * 4"));

        assertEquals("1 2 3 + +", infixToPostfix("1 + (2 + 3)"));
        assertEquals("1 2 + 3 +", infixToPostfix("(1 + 2) + 3"));
        assertEquals("1 2 + 3 *", infixToPostfix("(1 + 2) * 3"));
        assertEquals("1 2 3 + *", infixToPostfix("1 * (2 + 3)"));

        assertEquals("1 2 + 3 4 + *",
                infixToPostfix("(1 + 2) * (3 + 4)"));

        /* The following test cases are referenced here.
         * http://jcsites.juniata.edu/faculty/kruse/cs240/stackapps.htm
         */
        assertEquals("4 5 7 2 + - *",
                infixToPostfix("4 * (5 - (7 + 2))"));
        assertEquals("3 4 + 2 * 7 /",
                infixToPostfix("((3 + 4) * 2) / 7"));
        assertEquals("4 2 + 3 5 1 - * +",
                infixToPostfix("(4 + 2) + (3 * (5 - 1))"));
    }
}
