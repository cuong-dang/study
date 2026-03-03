package com.cuongd.study.algs.book.ch1;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.cuongd.study.algs.book.ch1.Ex1345.isPossiblePermutation;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Ex1345Test {
    @Test
    public void testOneElement() {
        assertTrue(isPossiblePermutation(List.of(1)));
    }

    @Test
    public void testContinuousPopping() {
        assertTrue(isPossiblePermutation(Arrays.asList(4, 3, 2, 1)));
        assertTrue(isPossiblePermutation(Arrays.asList(1, 2, 3, 4)));
    }

    @Test
    public void testGivenCases() {
        assertTrue(isPossiblePermutation(
                Arrays.asList(4, 3, 2, 1, 0, 9, 8, 7, 6, 5)));
        assertFalse(isPossiblePermutation(
                Arrays.asList(4, 6, 8, 7, 5, 3, 2, 9, 0, 1)));
        assertTrue(isPossiblePermutation(
                Arrays.asList(2, 5, 6, 7, 4, 8, 9, 3, 1, 0)));
        assertTrue(isPossiblePermutation(
                Arrays.asList(4, 3, 2, 1, 0, 5, 6, 7, 8, 9)));
        assertTrue(isPossiblePermutation(
                Arrays.asList(1, 2, 3, 4, 5, 6, 9, 8, 7, 0)));
        assertFalse(isPossiblePermutation(
                Arrays.asList(0, 4, 6, 5, 3, 8, 1, 7, 2, 9)));
        assertFalse(isPossiblePermutation(
                Arrays.asList(1, 4, 7, 9, 8, 6, 5, 3, 0, 2)));
        assertTrue(isPossiblePermutation(
                Arrays.asList(2, 1, 4, 3, 6, 5, 8, 7, 9, 0)));
    }

    /* Regression cases */
    @Test
    public void testRepeatNumbers() {
        assertFalse(isPossiblePermutation(Arrays.asList(4, 5, 4)));
    }

    @Test
    public void testRepeatNumbersWithGap() {
        assertFalse(isPossiblePermutation(Arrays.asList(3, 5, 7, 6, 4, 7)));
    }
}
