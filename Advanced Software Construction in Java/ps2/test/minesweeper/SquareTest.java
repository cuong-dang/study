package minesweeper;

import org.junit.Test;

import static org.junit.Assert.*;

/** Test suite for Square class. */
public class SquareTest {
    /* Test strategy */
    // - Constructor
    // - Dig op
    // - Flag op
    // - Deflag op
    // - setNumSurroundingBombs
    // - toString

    /* Constructor */
    @Test
    public void testDefaultInitializationState() {
        Square s = new Square(false, 1);
        assertFalse(s.hasBomb());
        assertEquals(1, s.numSurroundingBombs());
        assertEquals(Square.State.UNTOUCHED, s.state());

        Square ss = new Square(true, -1);
        assertTrue(ss.hasBomb());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalNumSurroundingBombs() {
        new Square(false, -1);
    }

    /* Dig op */
    @Test
    public void testDig() {
        Square s = new Square(false, 0);
        /* Untouched */
        assertTrue(s.dig());
        assertEquals(Square.State.DUG, s.state());
        /* Dug */
        assertFalse(s.dig());
        /* Flagged */
        s = new Square(false, 0);
        s.flag();
        assertFalse(s.dig());
        /* Exploded */
        s = new Square(true, -1);
        s.dig();
        assertEquals(Square.State.EXPLODED, s.state());
    }

    /* Flag op */
    @Test
    public void testFlag() {
        Square s = new Square(false, 0);
        /* Untouched */
        assertTrue(s.flag());
        assertEquals(Square.State.FLAGGED, s.state());
        /* Flagged */
        assertFalse(s.dig());
        /* Dug */
        s = new Square(false, 0);
        s.dig();
        assertFalse(s.flag());
        /* Exploded */
        s = new Square(true, -1);
        s.dig();
        assertFalse(s.flag());
    }

    /* Deflag op */
    @Test
    public void testDeFlag() {
        Square s = new Square(false, 0);
        /* Untouched */
        assertFalse(s.deflag());
        /* Flagged */
        s.flag();
        assertTrue(s.deflag());
        assertEquals(Square.State.UNTOUCHED, s.state());
        /* Dug */
        s = new Square(false, 0);
        s.dig();
        assertFalse(s.deflag());
        /* Exploded */
        s = new Square(true, -1);
        s.dig();
        assertFalse(s.deflag());
    }

    /* setNumberOfSurroundingBombs */
    @Test
    public void testSetNumberOfSurroundingBombsValid() {
        Square s = new Square(false, 0);
        assertEquals(0, s.numSurroundingBombs());
        s.setNumSurroundingBombs(1);
        assertEquals(1, s.numSurroundingBombs());
        s = new Square(true, -1);
        s.setNumSurroundingBombs(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNumberOfSurroundingBombsInvalid1() {
        Square s = new Square(false, 0);
        s.setNumSurroundingBombs(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNumberOfSurroundingBombsInvalid2() {
        Square s = new Square(true, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNumberOfSurroundingBombsInvalid3() {
        Square s = new Square(true, -1);
        s.setNumSurroundingBombs(0);
    }

    /* toString */
    @Test
    public void testToStringNoBombs() {
        Square s = new Square(false, 0);
        assertEquals("-", s.toString());
        s.flag();
        assertEquals("F", s.toString());
        s.deflag();
        s.dig();
        assertEquals(" ", s.toString());

        s = new Square(false, 1);
        s.dig();
        assertEquals("1", s.toString());
    }

    @Test(expected = IllegalStateException.class)
    public void testToStringHasBomb() {
        Square s = new Square(true, -1);
        s.dig();
        s.toString();
    }
}
