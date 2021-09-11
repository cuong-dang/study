package com.cuongd.study.algsp1.book.ch1;

import org.junit.Test;

import static com.cuongd.study.algsp1.book.ch1.Ex139.insertParentheses;
import static org.junit.Assert.assertEquals;

public class Ex139Test {
    @Test
    public void testInsertParenthesesCommonCase() {
        assertEquals("((1 + 2) * ((3 - 4) * (5 - 6)))",
                insertParentheses("1 + 2) * 3 - 4) * 5 - 6)))"));
    }

    @Test
    public void testInsertParenthesesNoParens() {
        assertEquals("1 + 2", insertParentheses("1 + 2"));
    }

    @Test
    public void testInsertParenthesesOneParens() {
        assertEquals("(1 + 2)", insertParentheses("1 + 2)"));
    }

    @Test
    public void testInsertParenthesesOneExpressionMultipleParens() {
        assertEquals("((1 + 2))", insertParentheses("1 + 2))"));
    }

    @Test
    public void testInsertParenthesesMixedNumberOfParens() {
        assertEquals("((((1 + 2) * (3 + 4)) - (5 / 6)))",
                insertParentheses("1 + 2) * 3 + 4)) - 5 / 6)))"));
    }
}
