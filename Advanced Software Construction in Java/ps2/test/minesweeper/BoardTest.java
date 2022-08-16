/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** Tests for Minesweeper board */
public class BoardTest {
    
    /* Test strategy */
    // - Constructor
    //   - Bomb positions
    //   - Surrounding bombs
    // - Operations
    //   - Dig
    //   - Flag
    //   - Deflag
    //   - Surrounding bombs after digging
    // - Game state

    private Board b;

    @Before
    public void init() {
        b = new Board("./test/boards/beginner.txt");
    }

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /* Constructor */
    @Test
    public void testConstructorBeginnerBoard() {
        assertEquals(Board.State.ACTIVE, b.state());
        assertEquals(8, b.dim());
        assertEquals(10, b.numBombsInitial());
        assertEquals(0, b.numBombsUnflagged());
        assertEquals(0, b.numFlags());
        assertEquals(String.format(
                "- X X - X - - -%n" +
                "- X - - - X X -%n" +
                "- - - - - - - X%n" +
                "- - - - - - - -%n" +
                "X - - - - - - -%n" +
                "- - - - - - - -%n" +
                "- - - - - X - -%n" +
                "- - - - X - - -%n"), b.toString());
    }

    @Test
    public void testSurroundingBombs() {
        int[][] expectedSurroundingBombs = new int[][] {
                new int[] { 2, -1, -1, 2, -1, 3, 2, 1 },
                new int[] { 2, -1, 3, 2, 2, -1, -1, 2 },
                new int[] { 1, 1, 1, 0, 1, 2, 3, -1 },
                new int[] { 1, 1, 0, 0, 0, 0, 1, 1 },
                new int[] { -1, 1, 0, 0, 0, 0, 0, 0 },
                new int[] { 1, 1, 0, 0, 1, 1, 1, 0 },
                new int[] { 0, 0, 0, 1, 2, -1, 1, 0 },
                new int[] { 0, 0, 0, 1, -1, 2, 1, 0 },
        };
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                assertEquals(expectedSurroundingBombs[i][j], b.numSurroundingBombs(i, j));
            }
        }
    }

    /* Operations */
    @Test
    public void testDig() {
        assertEquals(b.dig(0, 0), Board.OpResult.OK); // dig untouched
        assertEquals(b.squareState(0, 0), Square.State.DUG);
        assertEquals(b.dig(0, 0), Board.OpResult.ILLEGAL_MOVE); // dig dug
        b.flag(0, 1);
        assertEquals(b.dig(0, 1), Board.OpResult.ILLEGAL_MOVE); // dig flagged
        assertEquals(b.dig(0, 1), Board.OpResult.BOOM); // dig exploded
        assertEquals(b.squareState(0, 0), Square.State.EXPLODED);
    }

    @Test
    public void testFlag() {
        assertEquals(b.flag(0, 0), Board.OpResult.OK); // flag untouched
        assertEquals(b.squareState(0, 0), Square.State.FLAGGED);
        assertEquals(b.flag(0, 0), Board.OpResult.ILLEGAL_MOVE); // flag flagged
        b.deflag(0, 0);
        b.dig(0, 0);
        assertEquals(b.flag(0, 0), Board.OpResult.ILLEGAL_MOVE); // flag dug
        b.dig(0, 1);
        assertEquals(b.flag(0, 1), Board.OpResult.ILLEGAL_MOVE); // flag exploded
    }

    @Test
    public void testDelag() {
        assertEquals(b.deflag(0, 0), Board.OpResult.ILLEGAL_MOVE); // deflag untouched
        b.flag(0, 0);
        assertEquals(b.deflag(0, 0), Board.OpResult.OK); // deflag flagged
        b.deflag(0, 0);
        b.dig(0, 0);
        assertEquals(b.deflag(0, 0), Board.OpResult.ILLEGAL_MOVE); // deflag dug
        b.dig(0, 1);
        assertEquals(b.deflag(0, 1), Board.OpResult.ILLEGAL_MOVE); // deflag exploded
    }

    @Test
    public void testSurroundingBombsAfterDigging() {
        assertEquals(2, b.numSurroundingBombs(0, 0));
        assertEquals(3, b.numSurroundingBombs(1, 2));
        b.dig(0, 1);
        assertEquals(1, b.numSurroundingBombs(0, 0));
        assertEquals(2, b.numSurroundingBombs(1, 2));
        b.dig(1, 1);
        assertEquals(0, b.numSurroundingBombs(0, 0));
        assertEquals(1, b.numSurroundingBombs(1, 2));
    }

    /* Game state */
    @Test
    public void testMidGameState() {
        assertEquals(b.state(), Board.State.ACTIVE);
        assertEquals(10, b.numBombsUnflagged());
        assertEquals(0, b.numFlags());
        b.dig(0, 0);
        assertEquals(10, b.numBombsUnflagged());
        assertEquals(1, b.numFlags());
        b.dig(0, 1);
        assertEquals(9, b.numBombsUnflagged());
        assertEquals(2, b.numFlags());
    }

    @Test
    public void testEndGameStateWon() {
        b.flag(0, 1);
        b.flag(0, 2);
        b.flag(0, 4);
        b.flag(1, 1);
        b.flag(1, 5);
        b.flag(1, 6);
        b.flag(2, 7);
        b.flag(4, 0);
        b.flag(6, 5);
        b.flag(7, 4);
        assertEquals(0, b.numBombsUnflagged());
        assertEquals(10, b.numFlags());
        assertEquals(Board.State.WON, b.state());

        assertEquals(Board.OpResult.GAME_ENDED, b.dig(0, 0));
        assertEquals(Board.OpResult.GAME_ENDED, b.flag(0, 0));
        assertEquals(Board.OpResult.GAME_ENDED, b.deflag(0, 0));
    }

    @Test
    public void testEndGameStateLost() {
        b.dig(0, 1);
        b.dig(0, 2);
        b.dig(0, 4);
        b.dig(1, 1);
        b.dig(1, 5);
        b.dig(1, 6);
        b.dig(2, 7);
        b.dig(4, 0);
        b.dig(6, 5);
        b.dig(7, 4);
        assertEquals(10, b.numBombsUnflagged());
        assertEquals(Board.State.LOST, b.state());

        assertEquals(Board.OpResult.GAME_ENDED, b.dig(0, 0));
        assertEquals(Board.OpResult.GAME_ENDED, b.flag(0, 0));
        assertEquals(Board.OpResult.GAME_ENDED, b.deflag(0, 0));
    }
}
