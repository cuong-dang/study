package com.cuongd.study.algs.book.ch3;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Ex358Test {
    @Test
    public void test() {
        Ex358<Character, Integer> st = new Ex358<>();

        st.put('a', 0);
        st.put('a', 1);
        assertEquals(0, (int) st.get('a'));
        List<Integer> vals = new ArrayList<>();
        vals.add(0);
        vals.add(1);
        assertEquals(vals, st.getAll('a'));
    }
}
