/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

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
    //   - Mid-game
    //   - End-game

    private Board b;

    @Before
    public void init() throws IOException {
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
        assertEquals(8, b.dimX());
        assertEquals(8, b.dimY());
        assertEquals(10, b.numBombsInitial());
        assertEquals(10, b.numBombsUnflagged());
        assertEquals(0, b.numFlags());
        assertEquals(String.format(
                "- - - - - - - -%n" +
                "- - - - - - - -%n" +
                "- - - - - - - -%n" +
                "- - - - - - - -%n" +
                "- - - - - - - -%n" +
                "- - - - - - - -%n" +
                "- - - - - - - -%n" +
                "- - - - - - - -%n"), b.toString());
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
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                assertEquals(expectedSurroundingBombs[i][j], b.numSurroundingBombs(i, j));
            }
        }
    }

    @Test
    public void testConstructRandomSizeBoard() {
        assertEquals(String.format(
                "- - -%n" +
                "- - -%n"), (new Board(2, 3)).toString()
        );
    }

    /* Operations */
    @Test
    public void testDig() {
        assertEquals(Board.OpResult.OK, b.dig(0, 0)); // dig untouched
        assertEquals(Square.State.DUG, b.squareState(0, 0));
        assertEquals(Board.OpResult.ILLEGAL_MOVE, b.dig(0, 0)); // dig dug
        b.flag(0, 1);
        assertEquals(Board.OpResult.ILLEGAL_MOVE, b.dig(0, 1)); // dig flagged
        b.deflag(0, 1);
        assertEquals(Board.OpResult.BOOM, b.dig(0, 1)); // dig exploded
        assertEquals(Square.State.EXPLODED, b.squareState(0, 1));
    }

    @Test
    public void testFlag() {
        assertEquals(Board.OpResult.OK, b.flag(0, 0)); // flag untouched
        assertEquals(Square.State.FLAGGED, b.squareState(0, 0));
        assertEquals(Board.OpResult.ILLEGAL_MOVE, b.flag(0, 0)); // flag flagged
        b.deflag(0, 0);
        b.dig(0, 0);
        assertEquals(Board.OpResult.ILLEGAL_MOVE, b.flag(0, 0)); // flag dug
        b.dig(0, 1);
        assertEquals(Board.OpResult.ILLEGAL_MOVE, b.flag(0, 1)); // flag exploded
    }

    @Test
    public void testDelag() {
        assertEquals(Board.OpResult.ILLEGAL_MOVE, b.deflag(0, 0)); // deflag untouched
        // The following is a regression. There was a case where numFlags was
        // decremented even though the move was illegal.
        assertEquals(0, b.numFlags());
        b.flag(0, 0);
        assertEquals(Board.OpResult.OK, b.deflag(0, 0)); // deflag flagged
        b.deflag(0, 0);
        b.dig(0, 0);
        assertEquals(Board.OpResult.ILLEGAL_MOVE, b.deflag(0, 0)); // deflag dug
        b.dig(0, 1);
        assertEquals(Board.OpResult.ILLEGAL_MOVE, b.deflag(0, 1)); // deflag exploded
    }

    @Ignore("We are not changing the number of surrounding bombs upon explosions for now.")
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
        b.flag(0, 0);
        assertEquals(10, b.numBombsUnflagged());
        assertEquals(1, b.numFlags());
        b.flag(0, 1);
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
        assertEquals(Board.State.FROZEN, b.state());

        assertEquals(Board.OpResult.GAME_OVER, b.dig(0, 0));
        assertEquals(Board.OpResult.GAME_OVER, b.flag(0, 0));
        assertEquals(Board.OpResult.GAME_OVER, b.deflag(0, 0));
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
        assertEquals(0, b.numBombsUnflagged());
        assertEquals(Board.State.FROZEN, b.state());

        assertEquals(Board.OpResult.GAME_OVER, b.dig(0, 0));
        assertEquals(Board.OpResult.GAME_OVER, b.flag(0, 0));
        assertEquals(Board.OpResult.GAME_OVER, b.deflag(0, 0));
    }
}
