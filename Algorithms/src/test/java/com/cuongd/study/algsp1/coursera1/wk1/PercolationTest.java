package com.cuongd.study.algsp1.coursera1.wk1;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PercolationTest {
    private static final int N = 3;
    private Percolation p;

    @Before
    public void setup() {
        p = new Percolation(N);
    }

    @Test
    public void testPercolateOpenSequentially() {
        p.open(1, 1);
        assertTrue(p.isOpen(1, 1));
        assertTrue(p.isFull(1, 1));
        assertFalse(p.percolates());
        p.open(2, 1);
        assertTrue(p.isOpen(2, 1));
        assertTrue(p.isFull(2, 1));
        assertFalse(p.percolates());
        p.open(3, 2);
        assertTrue(p.isOpen(3, 2));
        assertFalse(p.isFull(3, 2));
        assertFalse(p.percolates());
        p.open(3, 1);
        assertTrue(p.isOpen(3, 1));
        assertTrue(p.isFull(1, 1));
        assertTrue(p.percolates());
        assertEquals(4, p.numberOfOpenSites());
    }

    @Test
    public void testPercolateOpenNonSequentially() {
        p.open(2, 1);
        assertTrue(p.isOpen(2, 1));
        assertFalse(p.isFull(2, 1));
        assertFalse(p.percolates());
        p.open(1, 3);
        assertTrue(p.isOpen(1, 3));
        assertTrue(p.isFull(1, 3));
        assertFalse(p.percolates());
        p.open(1, 1);
        assertTrue(p.isOpen(1, 1));
        assertTrue(p.isFull(1, 1));
        assertTrue(p.isFull(2, 1));
        assertFalse(p.percolates());
        p.open(3, 3);
        assertTrue(p.isOpen(3, 3));
        assertFalse(p.isFull(3, 3));
        assertFalse(p.percolates());
        p.open(2, 3);
        assertTrue(p.isOpen(2, 3));
        assertTrue(p.isFull(2, 3));
        assertTrue(p.isFull(3, 3));
        assertTrue(p.percolates());
        assertEquals(5, p.numberOfOpenSites());
    }

    /* Regression */
    @Test
    public void testDuplicateOpenSite() {
        p.open(1, 1);
        p.open(1, 1);
        assertEquals(1, p.numberOfOpenSites());
    }

    @Test
    public void testNEquals1() {
        Percolation q = new Percolation(1);
        assertFalse(q.percolates());
        q.open(1, 1);
        assertTrue(q.percolates());
    }

    @Test
    public void testBackwash() {
        p.open(1, 1);
        p.open(2, 1);
        p.open(3, 1);
        p.open(3, 3);
        assertFalse(p.isFull(3, 3));
    }

    @Test
    public void testTopConnectedDoesNotMeanFull() {
        assertFalse(p.isFull(1, 1));
    }

    @Test
    public void testDoesPercolateMultiplePaths() {
        p.open(1, 1);
        p.open(2, 1);
        p.open(3, 1);
        assertTrue(p.percolates());
        p.open(1, 3);
        assertTrue(p.percolates());
    }
}
