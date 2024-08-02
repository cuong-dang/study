package edu.caltech.test.nanodb.sql;

import edu.caltech.nanodb.expressions.TupleLiteral;
import edu.caltech.nanodb.server.CommandResult;
import org.testng.annotations.Test;

@Test(groups = {"hw2"})
public class TestJoins extends SqlTestCase {
    public TestJoins() {
        super("setup_testNaturalUsingJoins");
    }

    public void testJoins() throws Throwable {
        CommandResult result;

        // JOIN with only one common column: a
        result = server.doCommand(
                "SELECT t1.*, t3.* FROM test_nuj_t1 t1 JOIN test_nuj_t3 t3 ON t1.a = t3.a", true);
        TupleLiteral[] expected1 = {
                new TupleLiteral(2, 20, 2, 200, 2000),
                new TupleLiteral(3, 30, 3, 300, 3000),
                new TupleLiteral(5, 50, 5, 500, 5000),
                new TupleLiteral(6, 60, 6, 600, 6000),
                new TupleLiteral(8, 80, 8, 800, 8000)
        };
        assert checkSizeResults(expected1, result);
        assert checkUnorderedResults(expected1, result);
        checkResultSchema(result, "t1.a", "t1.b", "t3.a", "t3.c", "t3.d");

        // JOIN with two common columns: a and c
        result = server.doCommand(
                "SELECT t2.*, t3.* FROM test_nuj_t2 t2 JOIN test_nuj_t3 t3 ON t2.a = t3.a AND t2.c = t3.c", true);
        TupleLiteral[] expected2 = {
                new TupleLiteral(3, 40, 300, 3, 300, 3000),
                new TupleLiteral(5, 60, 500, 5, 500, 5000),
                new TupleLiteral(6, 50, 600, 6, 600, 6000),
                new TupleLiteral(8, 80, 800, 8, 800, 8000),
                new TupleLiteral(9, 100, 900, 9, 900, 9000)
        };
        assert checkSizeResults(expected2, result);
        assert checkUnorderedResults(expected2, result);
        checkResultSchema(result, "t2.a", "t2.b", "t2.c", "t3.a", "t3.c", "t3.d");
    }

    public void testLeftJoins() throws Throwable {
        CommandResult result;

        // JOIN with only one common column: a
        result = server.doCommand(
                "SELECT t1.*, t3.* FROM test_nuj_t1 t1 LEFT JOIN test_nuj_t3 t3 ON t1.a = t3.a", true);
        TupleLiteral[] expected1 = {
                new TupleLiteral(1, 10, null, null, null),
                new TupleLiteral(2, 20, 2, 200, 2000),
                new TupleLiteral(3, 30, 3, 300, 3000),
                new TupleLiteral(4, 40, null, null, null),
                new TupleLiteral(5, 50, 5, 500, 5000),
                new TupleLiteral(6, 60, 6, 600, 6000),
                new TupleLiteral(7, 70, null, null, null),
                new TupleLiteral(8, 80, 8, 800, 8000)
        };
        assert checkSizeResults(expected1, result);
        assert checkUnorderedResults(expected1, result);
        checkResultSchema(result, "t1.a", "t1.b", "t3.a", "t3.c", "t3.d");

        // JOIN with two common columns: a and c
        result = server.doCommand(
                "SELECT t2.*, t3.* FROM test_nuj_t2 t2 LEFT JOIN test_nuj_t3 t3 ON t2.a = t3.a AND t2.c = t3.c", true);
        TupleLiteral[] expected2 = {
                new TupleLiteral(3, 40, 300, 3, 300, 3000),
                new TupleLiteral(4, 30, 400, null, null, null),
                new TupleLiteral(5, 60, 500, 5, 500, 5000),
                new TupleLiteral(6, 50, 600, 6, 600, 6000),
                new TupleLiteral(7, 70, 700, null, null, null),
                new TupleLiteral(8, 80, 800, 8, 800, 8000),
                new TupleLiteral(9, 100, 900, 9, 900, 9000),
                new TupleLiteral(10, 90, 1000, null, null, null)
        };
        assert checkSizeResults(expected2, result);
        assert checkUnorderedResults(expected2, result);
        checkResultSchema(result, "t2.a", "t2.b", "t2.c", "t3.a", "t3.c", "t3.d");
    }

    public void tesRightJoins() throws Throwable {
        CommandResult result;

        result = server.doCommand(
                "SELECT t3.*, t1.* FROM test_nuj_t3 t3 RIGHT JOIN test_nuj_t1 t1 ON t3.a = t1.a", true);
        TupleLiteral[] expected1 = {
                new TupleLiteral(null, null, null, 1, 10),
                new TupleLiteral(2, 200, 2000, 2, 20),
                new TupleLiteral(3, 300, 3000, 3, 30),
                new TupleLiteral(null, null, null, 4, 40),
                new TupleLiteral(5, 500, 5000, 5, 50),
                new TupleLiteral(6, 600, 6000, 6, 60),
                new TupleLiteral(null, null, null, 7, 70),
                new TupleLiteral(8, 800, 8000, 8, 80)
        };
        assert checkSizeResults(expected1, result);
        assert checkUnorderedResults(expected1, result);
        checkResultSchema(result, "t3.a", "t3.c", "t3.d", "t1.a", "t1.b");
    }
}
