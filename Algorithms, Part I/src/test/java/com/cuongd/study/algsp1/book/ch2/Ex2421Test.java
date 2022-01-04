package com.cuongd.study.algsp1.book.ch2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex2421Test {
    @Test
    public void testPqStack() {
        Ex2421<Integer> stack = new Ex2421<>(16);
        stack.push(1);
        assertEquals(1, (int) stack.pop());

        stack.push(2);
        stack.push(3);

        assertEquals(3, (int) stack.pop());
        assertEquals(2, (int) stack.pop());
    }
}
