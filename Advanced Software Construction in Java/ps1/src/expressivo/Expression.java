package expressivo;

import lib6005.parser.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * An immutable data type representing a polynomial expression of:
 *   + and *
 *   nonnegative integers and floating-point numbers
 *   variables (case-sensitive nonempty strings of letters)
 * 
 * <p>PS1 instructions: this is a required ADT interface.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You may, however, add additional methods, or strengthen the specs of existing methods.
 * Declare concrete variants of Expression in their own Java source files.
 */
public interface Expression {
    
    // Datatype definition
    // Expression = Number(n: Integer) +
    //              Number(n: Double) +
    //              Sum(Expression: left, Expression: right) +
    //              Product(Expression: left, Expression: right)
    
    /**
     * Parse an expression.
     * @param input expression to parse, as defined in the PS1 handout.
     * @return expression AST for the input
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static Expression parse(String input) {
        try {
            return buildAst(generateParseTree(input));
        } catch (UnableToParseException e) {
            throw new IllegalArgumentException(e);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    /**
     * @return a parsable representation of this expression, such that
     * for all e:Expression, e.equals(Expression.parse(e.toString())).
     */
    @Override 
    public String toString();

    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are structurally-equal
     * Expressions, as defined in the PS1 handout.
     */
    @Override
    public boolean equals(Object thatObject);
    
    /**
     * @return hash code value consistent with the equals() definition of structural
     * equality, such that for all e1,e2:Expression,
     *     e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    public int hashCode();
    
    /* Parser */
    public enum ExpressionGrammar {ROOT, SUM, TERM, PRODUCT, PRIMITIVE, INTEGER, DOUBLE, VARIABLE, WHITESPACE};

    public static ParseTree<ExpressionGrammar> generateParseTree(String input)
            throws UnableToParseException, IOException {
        Parser<ExpressionGrammar> parser = GrammarCompiler.compile(new File("./src/expressivo/Expression.g"), ExpressionGrammar.ROOT);
        return parser.parse(input);
    }

    public static Expression buildAst(ParseTree<ExpressionGrammar> p) throws
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        switch (p.getName()) {
            case INTEGER:
                return new NumberInteger(Integer.parseInt(p.getContents()));
            case DOUBLE:
                return new NumberDouble(Double.parseDouble(p.getContents()));
            case VARIABLE:
                return new Variable(p.getContents());
            case PRIMITIVE:
                if (!p.childrenByName(ExpressionGrammar.INTEGER).isEmpty()) {
                    return buildAst(p.childrenByName(ExpressionGrammar.INTEGER).get(0));
                } else if (!p.childrenByName(ExpressionGrammar.DOUBLE).isEmpty()) {
                    return buildAst(p.childrenByName(ExpressionGrammar.DOUBLE).get(0));
                } else if (!p.childrenByName(ExpressionGrammar.SUM).isEmpty()) {
                    return buildAst(p.childrenByName(ExpressionGrammar.SUM).get(0));
                } else if (!p.childrenByName(ExpressionGrammar.VARIABLE).isEmpty()) {
                    return buildAst(p.childrenByName(ExpressionGrammar.VARIABLE).get(0));
                } else if (!p.childrenByName(ExpressionGrammar.PRODUCT).isEmpty()) {
                    return buildAst(p.childrenByName(ExpressionGrammar.PRODUCT).get(0));
                }
                throw new IllegalAccessException(
                        String.format("Unreachable code: Missing primitive case %s", p.getName()));
            case SUM:
                return buildBinaryOp(p, Sum.class);
            case TERM:
                if (!p.childrenByName(ExpressionGrammar.PRODUCT).isEmpty()) {
                    return buildBinaryOp(p.childrenByName(ExpressionGrammar.PRODUCT).get(0), Product.class);
                } else {
                    return buildAst(p.childrenByName(ExpressionGrammar.PRIMITIVE).get(0));
                }
            case PRODUCT:
                return buildBinaryOp(p, Product.class);
            case ROOT:
                return buildAst(p.childrenByName(ExpressionGrammar.SUM).get(0));
            case WHITESPACE:
                throw new IllegalStateException("Unreachable code: buildAst > case WHITESPACE");
        }
        throw new IllegalStateException("Unreachable code: buildAst > end");
    }

    public static Expression buildBinaryOp(ParseTree<ExpressionGrammar> p, Class<? extends BinaryOp> cls) throws
            NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        boolean first = true;
        Expression result = null;
        for (ParseTree<ExpressionGrammar> child : p.children()) {
            if (child.getName() == ExpressionGrammar.WHITESPACE) {
                continue;
            }
            if (first) {
                result = buildAst(child);
                first = false;
            } else {
                result = cls.getDeclaredConstructor(Expression.class, Expression.class)
                        .newInstance(result, buildAst(child));
            }
        }
        if (first) {
            throw new IllegalArgumentException("Binary op with 1 term");
        }
        return result;
    }
    
    /* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires permission of course staff.
     */
}
