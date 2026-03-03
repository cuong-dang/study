package com.cuongd.study.algs.book.ch3;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex3421Test {
    @Test
    public void test() {
        LinearProbingST<Character, Integer> st = new LinearProbingST<>(16, false);
        st.put('S', 0);
        st.put('E', 0);
        st.put('A', 0);
        st.put('R', 0);
        st.put('C', 0);
        st.put('H', 0);
        st.put('X', 0);
        st.put('M', 0);
        st.put('P', 0);
        st.put('L', 0);
        assertEquals((double) (16 + 1 + 9 + 8 + 7 + 6 + 5 + 4 + 3 + 2 + 1) / 16, st.avgMissProbes(), 0);
    }
}
