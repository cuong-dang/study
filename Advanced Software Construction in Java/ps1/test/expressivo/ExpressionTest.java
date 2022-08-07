/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import static org.junit.Assert.*;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

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
    //   - Product expression with sum groupings
    // - Test equals()
    //   - Equal integers
    //   - Equal doubles
    //   - Equal integer and doubles
    //   - Equal sums with two and more than two terms
    //   - Equal products with two and more than two terms
    //   - Equal products with sum groupings
    // - Test hashCode()
    //   - Same number hash codes
    //   - Same sum and product hash codes
    // - Test parse()
    //   - Integer and double numbers
    //   - Sum/product with two terms
    //   - Sum/product with more than two terms
    //   - Sum/product with groupings
    //   - Sum and product together
    //   - General mixed expressions

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
    public void testToStringSumTwoTerms() {
        assertEquals("1 + 1.0000", new Sum(new NumberInteger(1), new NumberDouble(1.0)).toString());
    }

    @Test
    public void testToStringProductTwoTerms() {
        assertEquals("1*1.0000", new Product(new NumberInteger(1), new NumberDouble(1.0)).toString());
    }

    @Test
    public void testToStringSumMoreThanTwoTerms() {
        assertEquals("1 + 1.0000 + 2", new Sum(new NumberInteger(1), new Sum(new NumberDouble(1.0),
                new NumberInteger(2))).toString());
    }

    @Test
    public void testToStringProductMoreThanTwoTerms() {
        assertEquals("1*1.0000*2", new Product(new NumberInteger(1), new Product(new NumberDouble(1.0),
                new NumberInteger(2))).toString());
    }

    @Test
    public void testToStringProductExpressionWithGroupings() {
        assertEquals("1*(1.0000 + 2)", new Product(new NumberInteger(1), new Sum(new NumberDouble(1.0),
                new NumberInteger(2))).toString());
    }

    /* Test equals() */
    @Test
    public void testEqualsIntegers() {
        assertEquals(new NumberInteger(1), new NumberInteger(1));
        assertNotEquals(new NumberInteger(1), new NumberInteger(2));
    }

    @Test
    public void testEqualsDouble() {
        assertEquals(new NumberDouble(1.0), new NumberDouble(1.0));
        assertNotEquals(new NumberDouble(1.0), new NumberDouble(2.0));
    }

    @Test
    public void testEqualsIntegerAndDouble() {
        assertEquals(new NumberInteger(1), new NumberDouble(1.0));
        assertEquals(new NumberDouble(1.0), new NumberInteger(1));
    }

    @Test
    public void testEqualsSums() {
        assertEquals(new Sum(new NumberInteger(1), new NumberInteger(2)),
                new Sum(new NumberInteger(1), new NumberInteger(2)));
        assertEquals(new Sum(new NumberInteger(1), new Sum(new NumberInteger(2), new NumberInteger(3))),
                new Sum(new NumberInteger(1), new Sum(new NumberInteger(2), new NumberInteger(3))));
    }

    @Test
    public void testEqualsProducts() {
        assertEquals(new Product(new NumberInteger(1), new NumberInteger(2)),
                new Product(new NumberInteger(1), new NumberInteger(2)));
        assertEquals(new Product(new NumberInteger(1), new Product(new NumberInteger(2), new NumberInteger(3))),
                new Product(new NumberInteger(1), new Product(new NumberInteger(2), new NumberInteger(3))));
    }

    @Test
    public void testEqualsProductsWithSumGroupings() {
        assertEquals(new Product(new NumberInteger(1), new Sum(new NumberInteger(2), new NumberInteger(3))),
                new Product(new NumberInteger(1), new Sum(new NumberInteger(2), new NumberInteger(3))));
    }

    /* Test hashCode() */
    @Test
    public void testHashCodeNumbers() {
        assertEquals(new NumberInteger(1).hashCode(), new NumberInteger(1).hashCode());
        assertEquals(new NumberDouble(1.0).hashCode(), new NumberDouble(1.0).hashCode());

        assertEquals(new NumberInteger(1).hashCode(), new NumberDouble(1.0).hashCode());
    }

    @Test
    public void testHashCodeSumsAndProducts() {
        assertEquals(new Sum(new NumberInteger(1), new NumberInteger(2)).hashCode(),
                new Sum(new NumberInteger(1), new NumberInteger(2)).hashCode());
        assertEquals(new Product(new NumberInteger(1), new NumberInteger(2)).hashCode(),
                new Product(new NumberInteger(1), new NumberInteger(2)).hashCode());

        assertEquals(new Product(new NumberInteger(1), new Sum(new NumberInteger(2), new NumberInteger(3))).hashCode(),
                new Product(new NumberInteger(1), new Sum(new NumberInteger(2), new NumberInteger(3))).hashCode());
    }

    /* Test parse() */
    @Test
    public void testParseNumbers() {
        assertEquals(new NumberInteger(1), Expression.parse("1"));
        assertEquals(new NumberInteger(1), Expression.parse("(1)"));
        assertEquals(new NumberDouble(1.0), Expression.parse("1.0"));
        assertEquals(new NumberDouble(1.0), Expression.parse("(1.0)"));
        assertEquals(new NumberDouble(1.2345), Expression.parse("1.2345"));
        assertEquals(new NumberDouble(1.234567), Expression.parse("1.234567"));
    }

    @Test
    public void testParseBinaryOpWithTwoTerms() throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        testParseBinaryOpWithTwoTermsHelper(Sum.class, new NumberInteger(1), new NumberInteger(2), "1 + 2");
        testParseBinaryOpWithTwoTermsHelper(Product.class, new NumberDouble(1.2), new NumberDouble(3.4), "1.2 * 3.4");
    }

    @Test
    public void testParseBinaryOpWithMoreThanTwoTerms() throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        testParseBinaryOpWithMoreThanTwoTermsHelper(Sum.class, new NumberInteger(1), new NumberInteger(2),
                new NumberInteger(3), "1 + 2 + 3");
        testParseBinaryOpWithMoreThanTwoTermsHelper(Sum.class, new NumberInteger(1), new NumberInteger(2),
                new NumberInteger(3), "(1 + 2) + 3");
        testParseBinaryOpWithMoreThanTwoTermsHelper(Product.class, new NumberDouble(1.2), new NumberDouble(3.4),
                new NumberDouble(5.6), "1.2 * 3.4 * 5.6");
        testParseBinaryOpWithMoreThanTwoTermsHelper(Product.class, new NumberDouble(1.2), new NumberDouble(3.4),
                new NumberDouble(5.6), "(1.2 * 3.4) * 5.6");
    }

    @Test
    public void testParseBinaryOpWithMoreThanTwoTermsRightGrouping() throws InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        testParseBinaryOpWithMoreThanTwoTermsRightGroupingHelper(Sum.class, new NumberInteger(1), new NumberInteger(2),
                new NumberInteger(3), "1 + (2 + 3)");
        testParseBinaryOpWithMoreThanTwoTermsRightGroupingHelper(Product.class, new NumberDouble(1.2),
                new NumberDouble(3.4), new NumberDouble(5.6), "1.2 * (3.4 * 5.6)");
    }

    @Test
    public void testParseSumAndProductTogether() {
        assertEquals(new Sum(new NumberInteger(1), new Product(new NumberInteger(2), new NumberInteger(3))),
                Expression.parse("1 + 2 * 3"));
        assertEquals(new Product(new Sum(new NumberInteger(1), new NumberInteger(2)), new NumberInteger(3)),
                Expression.parse("(1 + 2) * 3"));
    }

    @Test
    public void testGeneralMixedExpressions() {
        assertEquals(
            new Sum(
                new Sum(new NumberInteger(1), new Product(new NumberInteger(2), new NumberInteger(3))),
                new NumberInteger(4)
            ),
            Expression.parse("1 + 2 * 3 + 4")
        );
        assertEquals(
            new Sum(
                new Product(new NumberInteger(1), new NumberInteger(2)),
                new Product(new NumberInteger(3), new NumberInteger(4))
            ),
            Expression.parse("1 * 2 + 3 * 4")
        );
        assertEquals(
            new Product(
                new Product(new NumberInteger(1), new Sum(new NumberInteger(2), new NumberInteger(3))),
                new NumberInteger(4)
            ),
            Expression.parse("1 * (2 + 3) * 4")
        );
    }

    /* Helpers */
    private void testParseBinaryOpWithTwoTermsHelper(Class<? extends BinaryOp> binOp, Number left, Number right,
                                                     String input)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        assertEquals(binOp.getDeclaredConstructor(Expression.class, Expression.class).newInstance(left, right),
                Expression.parse(input));
    }

    private void testParseBinaryOpWithMoreThanTwoTermsHelper(Class<? extends BinaryOp> binOp, Number left, Number mid,
                                                             Number right, String input)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        assertEquals(binOp.getDeclaredConstructor(Expression.class, Expression.class)
                        .newInstance(binOp.getDeclaredConstructor(Expression.class, Expression.class)
                                .newInstance(left, mid), right),
                Expression.parse(input));
    }

    private void testParseBinaryOpWithMoreThanTwoTermsRightGroupingHelper(Class<? extends BinaryOp> binOp, Number left,
                                                                          Number mid, Number right, String input)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        assertEquals(binOp.getDeclaredConstructor(Expression.class, Expression.class)
                        .newInstance(left, binOp.getDeclaredConstructor(Expression.class, Expression.class)
                                .newInstance(mid, right)),
                Expression.parse(input));
    }
}
