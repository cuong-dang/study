package com.cuongd.study.algs.coursera1.wk4;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class BoardTest {
    private Board board;

    @Before
    public void init() {
        board = new Board(new int[][]{
                new int[]{8, 1, 3},
                new int[]{4, 0, 2},
                new int[]{7, 6, 5}
        });
    }

    @Test
    public void testToString() {
        System.out.println(board.toString());
    }

    @Test
    public void testDimension() {
        assertEquals(3, board.dimension());
    }

    @Test
    public void testHamming() {
        assertEquals(5, board.hamming());
    }

    @Test
    public void testManhattan() {
        assertEquals(10, board.manhattan());
    }

    @Test
    public void testIsGoal() {
        assertFalse(board.isGoal());
    }

    @Test
    public void testNeighbors() {
        assertEquals(Arrays.asList(
                new Board(new int[][]{
                        new int[]{8, 0, 3},
                        new int[]{4, 1, 2},
                        new int[]{7, 6, 5}}),
                new Board(new int[][]{
                        new int[]{8, 1, 3},
                        new int[]{4, 6, 2},
                        new int[]{7, 0, 5}}),
                new Board(new int[][]{
                        new int[]{8, 1, 3},
                        new int[]{0, 4, 2},
                        new int[]{7, 6, 5}}),
                new Board(new int[][]{
                        new int[]{8, 1, 3},
                        new int[]{4, 2, 0},
                        new int[]{7, 6, 5}})
        ), board.neighbors());
    }

    @Test
    public void testTwin() {
        assertEquals(new Board(new int[][]{
                new int[]{1, 8, 3},
                new int[]{4, 0, 2},
                new int[]{7, 6, 5}}), board.twin());
        assertEquals(new Board(new int[][]{
                new int[]{0, 3, 8},
                new int[]{4, 1, 2},
                new int[]{7, 6, 5}}), (new Board(new int[][]{
                new int[]{0, 8, 3},
                new int[]{4, 1, 2},
                new int[]{7, 6, 5}})).twin());
        assertEquals(new Board(new int[][]{
                new int[]{8, 0, 4},
                new int[]{3, 1, 2},
                new int[]{7, 6, 5}}), (new Board(new int[][]{
                new int[]{8, 0, 3},
                new int[]{4, 1, 2},
                new int[]{7, 6, 5}})).twin());
    }
}
