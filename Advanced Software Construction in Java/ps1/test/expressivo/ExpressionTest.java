/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import static org.junit.Assert.*;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

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
    //   - Variables
    // - Test equals()
    //   - Equal integers
    //   - Equal doubles
    //   - Equal integer and doubles
    //   - Equal sums with two and more than two terms
    //   - Equal products with two and more than two terms
    //   - Equal products with sum groupings
    //   - Variables
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
    //   - Variables

    // - Test differentiate()
    //   - Constants
    //   - Sums
    //   - Products
    //   - Mixed

    // - Test simplify()
    //   - Sums/products with constants
    //   - Sums/products with variables evaluated to constants
    //   - Sums/products with unevaluated variables

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

    @Test
    public void testToStringVariables() {
        assertEquals("x", new Variable("x").toString());
        assertEquals("x + 1", new Sum(new Variable("x"), new NumberInteger(1)).toString());
        assertEquals("2*x", new Product(new NumberInteger(2), new Variable("x")).toString());
        assertEquals(
            "2*(x + 1)",
            new Product(
                new NumberInteger(2),
                new Sum(new Variable("x"), new NumberInteger(1))
            ).toString()
        );
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

    @Test
    public void testEqualsVariables() {
        assertEquals(new Variable("x"), new Variable("x"));
        assertNotEquals(new Variable("x"), new Variable("y"));
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
    public void testParseGeneralMixedExpressions() {
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

    @Test
    public void testParseVariables() {
        assertEquals(new Variable("x"), Expression.parse("x"));
        assertEquals(new Sum(new Variable("x"), new NumberInteger(1)), Expression.parse("x + 1"));
        assertEquals(new Sum(new Variable("x"), new Variable("y")), Expression.parse("x + y"));
        assertEquals(
            new Product(
                new Sum(new Variable("x"), new NumberInteger(1)),
                new Variable("y")
            ),
            Expression.parse("(x + 1) * y")
        );
    }

    /* Test differentiate() */
    @Test
    public void testDifferentiateNumbers() {
        assertEquals(new NumberInteger(0), new NumberInteger(1).differentiate(new Variable("x")));
        assertEquals(new NumberInteger(0), new NumberDouble(1.0).differentiate(new Variable("x")));
    }

    @Test
    public void testDifferentiateSums() {
        assertEquals(Expression.parse("1 + 0"), Expression.parse("x + 1").differentiate(new Variable("x")));
        assertEquals(Expression.parse("1 + 0"), Expression.parse("x + y").differentiate(new Variable("x")));
    }

    @Test
    public void testDifferentiateProducts() {
        assertEquals(Expression.parse("2*1 + x*0"), Expression.parse("2*x").differentiate(new Variable("x")));
        assertEquals(Expression.parse("y*1 + x*0"), Expression.parse("y*x").differentiate(new Variable("x")));
        assertEquals(Expression.parse("2*0 + y*0"), Expression.parse("2*y").differentiate(new Variable("x")));
    }

    @Test
    public void testDifferentiateMixed() {
        assertEquals(Expression.parse("1 + (2*1 + x*0)"), Expression.parse("x + 2*x").differentiate(new Variable("x")));
    }

    /* Test simplify() */
    @Test
    public void testSimplifyConstants() {
        assertEquals(Expression.parse("1"), Expression.parse("1").simplify(new HashMap<>()));
        assertEquals(Expression.parse("3"), Expression.parse("1 + 2").simplify(new HashMap<>()));
        assertEquals(Expression.parse("2"), Expression.parse("1 * 2").simplify(new HashMap<>()));

        assertEquals(Expression.parse("5"), Expression.parse("1*2 + 3").simplify(new HashMap<>()));
        assertEquals(Expression.parse("11"), Expression.parse("1 + 2*3 + 4").simplify(new HashMap<>()));
        assertEquals(Expression.parse("14"), Expression.parse("2*(3 + 4)").simplify(new HashMap<>()));
    }

    @Test
    public void testSimplifyVariablesEvaluated() {
        Map<String, Double> env = new HashMap<>();
        env.put("x", 1.0);
        env.put("y", 2.0);

        assertEquals(Expression.parse("1"), Expression.parse("x").simplify(env));
        assertEquals(Expression.parse("2"), Expression.parse("1 + x").simplify(env));
        assertEquals(Expression.parse("2"), Expression.parse("2*x").simplify(env));
        assertEquals(Expression.parse("4"), Expression.parse("1 + x + y").simplify(env));
        assertEquals(Expression.parse("4"), Expression.parse("2*x*y").simplify(env));
        assertEquals(Expression.parse("8"), Expression.parse("2*x + 3*y").simplify(env));
    }

    @Test
    public void testSimplifyVariablesUnevaluated() {
        Map<String, Double> env = new HashMap<>();
        env.put("x", 1.0);
        env.put("y", 2.0);

        assertEquals(Expression.parse("1 + z"), Expression.parse("1 + z").simplify(env));
        assertEquals(Expression.parse("2 + z"), Expression.parse("1 + x + z").simplify(env));
        assertEquals(Expression.parse("1 + (1 + z)"), Expression.parse("1 + (x + z)").simplify(env));
        assertEquals(Expression.parse("1 + 2*z"), Expression.parse("1*x + 2*z").simplify(env));
        assertEquals(Expression.parse("3*z"), Expression.parse("1*(x + 2)*z").simplify(env));
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
