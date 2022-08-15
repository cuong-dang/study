package minesweeper;

import org.junit.Test;

import static org.junit.Assert.*;

/** Test suite for Square class. */
public class SquareTest {
    /* Test strategy */
    // - Default initialization state
    // - Dig op
    // - Flag op
    // - Deflag op

    @Test
    public void testDefaultInitializationState() {
        Square s = new Square(false);
        assertFalse(s.hasBomb());
        assertEquals(Square.State.UNTOUCHED, s.state());

        Square ss = new Square(true);
        assertTrue(ss.hasBomb());
    }

    /* Dig op */
    @Test
    public void testDig() {
        Square s = new Square(false);
        /* Untouched */
        assertTrue(s.dig());
        assertEquals(Square.State.DUG, s.state());
        /* Dug */
        assertFalse(s.dig());
        /* Flagged */
        s = new Square(false);
        s.flag();
        assertFalse(s.dig());
        /* Exploded */
        s = new Square(true);
        s.dig();
        assertEquals(Square.State.EXPLODED, s.state());
    }

    /* Flag op */
    @Test
    public void testFlag() {
        Square s = new Square(false);
        /* Untouched */
        assertTrue(s.flag());
        assertEquals(Square.State.FLAGGED, s.state());
        /* Flagged */
        assertFalse(s.dig());
        /* Dug */
        s = new Square(false);
        s.dig();
        assertFalse(s.flag());
        /* Exploded */
        s = new Square(true);
        s.dig();
        assertFalse(s.flag());
    }

    /* Deflag op */
    @Test
    public void testDeFlag() {
        Square s = new Square(false);
        /* Untouched */
        assertFalse(s.deflag());
        /* Flagged */
        s.flag();
        assertTrue(s.deflag());
        assertEquals(Square.State.UNTOUCHED, s.state());
        /* Dug */
        s = new Square(false);
        s.dig();
        assertFalse(s.deflag());
        /* Exploded */
        s = new Square(true);
        s.dig();
        assertFalse(s.deflag());
    }
}
