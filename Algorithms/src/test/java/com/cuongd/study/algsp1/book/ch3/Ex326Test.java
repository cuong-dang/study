package com.cuongd.study.algsp1.book.ch3;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex326Test {
    private Ex326<String, Integer> st;

    @Before
    public void init() {
        st = new Ex326<>();
    }

    @Test
    public void testHeight1() {
        assertEquals(0, st.height1());
        st.put("M", 0);
        assertEquals(1, st.height1());
        st.put("N", 0);
        assertEquals(2, st.height1());
        st.put("O", 0);
        assertEquals(3, st.height1());
        st.put("A", 0);
        assertEquals(3, st.height1());
        st.put("B", 0);
        assertEquals(3, st.height1());
        st.put("C", 0);
        assertEquals(4, st.height1());
        st.put("D", 0);
        assertEquals(5, st.height1());
    }

    @Test
    public void testHeight2() {
        assertEquals(0, st.height2());
        st.put("M", 0);
        assertEquals(1, st.height2());
        st.put("N", 0);
        assertEquals(2, st.height2());
        st.put("O", 0);
        assertEquals(3, st.height2());
        st.put("A", 0);
        assertEquals(3, st.height2());
        st.put("B", 0);
        assertEquals(3, st.height2());
        st.put("C", 0);
        assertEquals(4, st.height2());
        st.put("D", 0);
        assertEquals(5, st.height2());
    }
}
