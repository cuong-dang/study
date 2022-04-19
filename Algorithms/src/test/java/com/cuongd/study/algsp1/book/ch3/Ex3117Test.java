package com.cuongd.study.algsp1.book.ch3;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Ex3117Test {
    Ex3117<Integer, Integer> st;

    @Before
    public void init() {
        st = new Ex3117<>(10);
    }

    @Test
    public void testFloor() {
        st.put(0, 1);
        st.put(2, 3);
        st.put(6, 7);
        st.put(4, 5);

        assertNull(st.floor(-1));
        assertEquals(0, (int) st.floor(0));
        assertEquals(0, (int) st.floor(1));
        assertEquals(2, (int) st.floor(2));
        assertEquals(6, (int) st.floor(7));
    }
}
