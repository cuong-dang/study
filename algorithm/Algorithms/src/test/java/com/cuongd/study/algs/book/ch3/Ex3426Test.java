package com.cuongd.study.algs.book.ch3;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex3426Test {
    @Test
    public void testWithoutResizing() {
        LinearProbingST<Character, Integer> st = new LinearProbingST<>(8, false);
        // hash(_, 8) S: 6, E: 1, A: 5, R: 7, C: 7, H: 4, X: 5, M: 1, P: 5, L: 0
        st.put('S', 0);
        st.put('E', 0);
        st.put('A', 0);
        st.put('R', 0);
        st.put('C', 0);
        st.put('H', 0);
        st.delete2('A');
        st.put('X', 0);
        assertEquals('X', (char) st.keyAt(2));
    }

    @Test
    public void testWithResizing() {
        LinearProbingST<Character, Integer> st = new LinearProbingST<>(8, true);
        // hash(_, 8) S: 6, E: 1, A: 5, R: 7, C: 7, H: 4, X: 5, M: 1, P: 5, L: 0
        st.put('S', 0);
        st.put('E', 0);
        st.put('A', 0);
        assertEquals(8, st.m());
        assertEquals(3, st.n());
        assertEquals(3, st.nWithTombs());
        st.delete2('S');
        assertEquals(8, st.m());
        assertEquals(2, st.n());
        assertEquals(3, st.nWithTombs());
        st.put('R', 0);
        st.put('C', 0);
        assertEquals(16, st.m());
        assertEquals(4, st.n());
        assertEquals(4, st.nWithTombs());
    }
}
