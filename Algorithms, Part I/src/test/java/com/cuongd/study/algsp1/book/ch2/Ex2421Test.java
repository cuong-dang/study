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

    @Test
    public void testPqStackArrayResize() {
        Ex2421<Integer> stack = new Ex2421<>();
        assertEquals(4, stack.capacity());
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(4, stack.capacity());
        stack.push(4);
        assertEquals(8, stack.capacity());
        stack.push(5);
        stack.push(6);
        stack.push(7);
        assertEquals(7, (int) stack.pop());
        assertEquals(6, (int) stack.pop());
        assertEquals(5, (int) stack.pop());
        assertEquals(4, (int) stack.pop());
        assertEquals(3, (int) stack.pop());
        assertEquals(4, stack.capacity());
        assertEquals(2, (int) stack.pop());
        assertEquals(1, (int) stack.pop());
    }
}
