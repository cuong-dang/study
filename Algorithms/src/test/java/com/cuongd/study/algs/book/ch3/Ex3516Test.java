package com.cuongd.study.algs.book.ch3;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex3516Test {
    @Test
    public void test() {
        Ex3516 sv1 = new Ex3516();
        Ex3516 sv2 = new Ex3516();
        sv1.put(0, 1);
        sv1.put(1, -1);
        sv1.put(2, 2);
        sv2.put(1, 1);
        sv2.put(2, 3);
        sv2.put(5, 5);
        Ex3516 ans = sv1.sum(sv2);
        assertEquals(3, ans.size());
        assertEquals(1, (int) ans.get(0));
        assertEquals(5, (int) ans.get(2));
        assertEquals(5, (int) ans.get(5));
    }
}
