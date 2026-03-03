package com.cuongd.study.algs.book.ch3;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Ex3237Test {
    @Test
    public void testPrintLevel() {
        Ex3237<String, Integer> st = new Ex3237<>();
        List<String> keys = new ArrayList<>();
        st.put("S", 0); st.put("E", 1); st.put("A", 2); st.put("R", 3); st.put("C", 4);
        st.put("H", 5); st.put("E", 6); st.put("X", 7); st.put("A", 8); st.put("M", 9);
        st.put("P", 10); st.put("L", 11); st.put("E", 12);
        st.printLevel().forEach(keys::add);
        assertEquals(Arrays.asList("S", "E", "X", "A", "R", "C", "H", "M", "L", "P"), keys);
    }
}
