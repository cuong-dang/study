package com.cuongd.study.algsp1.book.ch3;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Ex3116Test {
    Ex3116<Integer, Integer> st;

    @Before
    public void init() {
        st = new Ex3116<>(10);
    }

    @Test
    public void testDelete() {
        st.put(0, 1);
        st.put(2, 3);
        st.put(6, 7);
        st.put(4, 5);

        assertEquals(5, (int) st.get(4));
        st.delete(4);
        assertNull(st.get(4));
    }
}
