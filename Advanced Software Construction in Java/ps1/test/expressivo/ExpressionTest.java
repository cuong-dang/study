/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for the Expression abstract data type.
 */
public class ExpressionTest {

    // Testing strategy
    // - Test toString()
    //   - Primitive integer string representation
    //   - Primitive double string representation (4 decimal places)
    //   - Sum expression with two terms (integer and double)
    //   - Product expression with two terms (integer and double)
    //   - Sum expression with more than two terms
    //   - Product expression with more than two terms
    //   - Product expression with groupings
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    /* Test toString() */
    @Test
    public void testToStringPrimitiveInteger() {
        assertEquals("1", new NumberInteger(1).toString());
    }

    @Test
    public void testToStringPrimitiveDoubleFewerThan4DecimalPlaces() {
        assertEquals("1.0000", new NumberDouble(1.0).toString());
    }

    @Test
    public void testToStringPrimitiveDoubleMoreThan4DecimalPlaces() {
        assertEquals("1.2346", new NumberDouble(1.23456).toString());
    }

    @Test
    public void testSumTwoTerms() {
        assertEquals("1 + 1.0000", new Sum(new NumberInteger(1), new NumberDouble(1.0)).toString());
    }

    @Test
    public void testProductTwoTerms() {
        assertEquals("1*1.0000", new Product(new NumberInteger(1), new NumberDouble(1.0)).toString());
    }

    @Test
    public void testSumMoreThanTwoTerms() {
        assertEquals("1 + 1.0000 + 2", new Sum(new NumberInteger(1), new Sum(new NumberDouble(1.0),
                new NumberInteger(2))).toString());
    }

    @Test
    public void testProductMoreThanTwoTerms() {
        assertEquals("1*1.0000*2", new Product(new NumberInteger(1), new Product(new NumberDouble(1.0),
                new NumberInteger(2))).toString());
    }

    @Test
    public void testProductExpressionWithGroupings() {
        assertEquals("1*(1.0000 + 2)", new Product(new NumberInteger(1), new Sum(new NumberDouble(1.0),
                new NumberInteger(2))).toString());
    }
}
