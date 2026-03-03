package com.cuongd.study.algs.book.ch2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex2520Test {
    @Test
    public void testAllSeparated() {
        Ex2520.Job[] jobs = new Ex2520.Job[]{new Ex2520.Job(0, 2), new Ex2520.Job(3, 4)};
        assertEquals(2, Ex2520.longestProcessingTime(jobs, new QuickSort()));
    }

    @Test
    public void testOverlapped1() {
        Ex2520.Job[] jobs = new Ex2520.Job[]{new Ex2520.Job(0, 2), new Ex2520.Job(1, 3),
                new Ex2520.Job(4, 5)};
        assertEquals(3, Ex2520.longestProcessingTime(jobs, new QuickSort()));
    }

    @Test
    public void testOverlapped2() {
        Ex2520.Job[] jobs = new Ex2520.Job[]{new Ex2520.Job(0, 2), new Ex2520.Job(1, 3),
                new Ex2520.Job(4, 8)};
        assertEquals(4, Ex2520.longestProcessingTime(jobs, new QuickSort()));
    }
}
