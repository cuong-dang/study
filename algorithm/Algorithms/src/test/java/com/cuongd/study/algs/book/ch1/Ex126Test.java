package com.cuongd.study.algs.book.ch1;

import org.junit.Test;

import static com.cuongd.study.algs.book.ch1.Ex126.isCircularRotation;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Ex126Test {
    @Test
    public void testIsCircularRotation() {
        assertTrue(isCircularRotation("", ""));
        assertTrue(isCircularRotation("a", "a"));
        assertTrue(isCircularRotation("ACTGACG", "TGACGAC"));
        assertTrue(isCircularRotation("aaccb", "ccbaa"));
        assertFalse(isCircularRotation("abc", "bc"));
        assertFalse(isCircularRotation("aabbc", "abbca"));
    }
}
