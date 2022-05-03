package com.cuongd.study.algsp1.book.ch1;

import edu.princeton.cs.algs4.In;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static com.cuongd.study.algsp1.book.ch1.Ex1415.threeSumFaster;
import static com.cuongd.study.algsp1.book.ch1.Ex1415.twoSumFaster;
import static org.junit.Assert.assertEquals;

public class Ex1415Test {
    private static final String ONE_KINTS_PATH = "./src/main/resources/ch1/1Kints.txt";
    private static final String TWO_KINTS_PATH = "./src/main/resources/ch1/2Kints.txt";
    private static final String FOUR_KINTS_PATH = "./src/main/resources/ch1/4Kints.txt";
    private static final String EIGHT_KINTS_PATH = "./src/main/resources/ch1/8Kints.txt";
    private static final String ONE_MINTS_PATH = "./src/main/resources/ch1/1Mints.txt";

    @Test
    public void testTwoSumNoPairs() {
        assertEquals(0, twoSumFaster(new int[]{}));
        assertEquals(0, twoSumFaster(new int[]{1, 2, 3}));
    }

    @Test
    public void testTwoSumOnePair() {
        assertEquals(1, twoSumFaster(new int[]{-1, 0, 1}));
        assertEquals(1, twoSumFaster(new int[]{-2, -1, 1, 3}));
        assertEquals(1, twoSumFaster(new int[]{-2, -1, 0, 2}));
    }

    @Test
    public void testTwoSumMultiplePairs() {
        assertEquals(2, twoSumFaster(new int[]{-2, -1, 0, 1, 2}));
        assertEquals(3, twoSumFaster(new int[]{-5, -4, -2, -1, 2, 4, 5}));
    }

    @Test
    public void twoSum1KInts() {
        int[] a = new In(new File(ONE_KINTS_PATH)).readAllInts();
        Arrays.sort(a);
        assertEquals(1, twoSumFaster(a));
    }

    @Test
    public void twoSum2KInts() {
        int[] a = new In(new File(TWO_KINTS_PATH)).readAllInts();
        Arrays.sort(a);
        assertEquals(2, twoSumFaster(a));
    }

    @Test
    public void twoSum4KInts() {
        int[] a = new In(new File(FOUR_KINTS_PATH)).readAllInts();
        Arrays.sort(a);
        assertEquals(3, twoSumFaster(a));
    }

    @Test
    public void twoSum8KInts() {
        int[] a = new In(new File(EIGHT_KINTS_PATH)).readAllInts();
        Arrays.sort(a);
        assertEquals(19, twoSumFaster(a));
    }

    @Test
    public void twoSum1MInts() {
        int[] a = new In(new File(ONE_MINTS_PATH)).readAllInts();
        Arrays.sort(a);
        assertEquals(249838, twoSumFaster(a));
    }

    @Test
    public void testThreeSumFasterNoResult() {
        assertEquals(0, threeSumFaster(new int[]{}));
        assertEquals(0, threeSumFaster(new int[]{1, 2, 3}));
    }

    @Test
    public void testThreeSumFasterOneResult() {
        assertEquals(0, threeSumFaster(new int[]{}));
        assertEquals(1, threeSumFaster(new int[]{-3, 1, 2}));
    }

    @Test
    public void testThreeSumFasterMultipleResults() {
        assertEquals(5, threeSumFaster(new int[]{-9, -8, -6, -4, 1, 2, 3, 4, 5}));
    }

    @Test
    public void threeSum1KInts() {
        int[] a = new In(new File(ONE_KINTS_PATH)).readAllInts();
        Arrays.sort(a);
        assertEquals(70, threeSumFaster(a));
    }

    @Test
    public void threeSum2KInts() {
        int[] a = new In(new File(TWO_KINTS_PATH)).readAllInts();
        Arrays.sort(a);
        assertEquals(528, threeSumFaster(a));
    }

    @Test
    public void threeSum4KInts() {
        int[] a = new In(new File(FOUR_KINTS_PATH)).readAllInts();
        Arrays.sort(a);
        assertEquals(4039, threeSumFaster(a));
    }

    @Test
    public void threeSum8KInts() {
        int[] a = new In(new File(EIGHT_KINTS_PATH)).readAllInts();
        Arrays.sort(a);
        assertEquals(32074, threeSumFaster(a));
    }
}
