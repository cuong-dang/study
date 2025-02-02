package edu.caltech.test.nanodb.sql;


import edu.caltech.nanodb.expressions.TupleLiteral;
import edu.caltech.nanodb.server.CommandResult;
import org.testng.annotations.Test;


/**
 * This class exercises the database with some simple EXISTS operations, to
 * verify that the most basic functionality works.
 **/
@Test(groups = {"sql", "hw5"})
public class TestExists extends SqlTestCase {
    public TestExists() {
        super("setup_testExists");
    }

    /**
     * This test checks that at least one value was successfully inserted into
     * each of the test tables.
     */
    public void testExistsTablesNotEmpty() {
        testTableNotEmpty("test_exists_1");
        testTableNotEmpty("test_exists_2");
    }

    /**
     * This method tests whether EXISTS and NOT EXISTS work as expected.  The
     * nested query is not correlated, so we only have a simple exercise of
     * the operators.
     *
     * @throws Exception if any query parsing or execution issues occur.
     */
    public void testExists() throws Throwable {
        CommandResult result;
        TupleLiteral[] expected1 = {
                createTupleFromNum(1),
                createTupleFromNum(2),
                createTupleFromNum(3),
                createTupleFromNum(4)
        };
        TupleLiteral[] expected2 = {};

        result = server.doCommand(
                "SELECT a FROM test_exists_1 WHERE " +
                        "EXISTS (SELECT b FROM test_exists_2 WHERE b > 20)", true);
        assert checkSizeResults(expected1, result);
        assert checkUnorderedResults(expected1, result);

        result = server.doCommand(
                "SELECT a FROM test_exists_1 WHERE " +
                        "NOT EXISTS (SELECT b FROM test_exists_2 WHERE b > 80)", true);
        assert checkSizeResults(expected1, result);
        assert checkUnorderedResults(expected1, result);

        result = server.doCommand(
                "SELECT a FROM test_exists_1 WHERE " +
                        "EXISTS (SELECT b FROM test_exists_2 WHERE b > 80)", true);
        assert checkSizeResults(expected2, result);
        assert checkUnorderedResults(expected2, result);

        result = server.doCommand(
                "SELECT a FROM test_exists_1 WHERE " +
                        "NOT EXISTS (SELECT b FROM test_exists_2 WHERE b > 20)", true);
        assert checkSizeResults(expected2, result);
        assert checkUnorderedResults(expected2, result);
    }

    public void testCorrelatedSelect() throws Throwable {
        TupleLiteral[] expected = {
                createTupleFromNum(1),
                createTupleFromNum(2),
                createTupleFromNum(3),
                createTupleFromNum(4)
        };
        CommandResult result;
        result = server.doCommand("SELECT (SELECT t2.a FROM test_exists_1 t2 WHERE t2.a = t1.a) a " +
                "FROM test_exists_1 t1", true);
        assert checkSizeResults(expected, result);
        assert checkUnorderedResults(expected, result);
    }

    public void testCorrelatedWhere() throws Throwable {
        TupleLiteral[] expected = {
                createTupleFromNum(1),
                createTupleFromNum(2),
                createTupleFromNum(3),
                createTupleFromNum(4)
        };
        CommandResult result;
        result = server.doCommand("SELECT * FROM test_exists_1 t1 " +
                "WHERE t1.a = (SELECT a FROM test_exists_1 t2 WHERE t2.a = t1.a)", true);
        assert checkSizeResults(expected, result);
        assert checkUnorderedResults(expected, result);
    }

    public void testCorrelatedHaving() throws Throwable {
        TupleLiteral[] expected = {
                new TupleLiteral(1, 1),
        };
        CommandResult result;
        result = server.doCommand("SELECT t1.a, count(*) AS count FROM test_exists_1 t1 GROUP BY t1.a " +
                "HAVING count(*) = (SELECT a FROM test_exists_1 t2 WHERE t2.a = t1.a)", true);
        assert checkSizeResults(expected, result);
        assert checkUnorderedResults(expected, result);
        checkResultSchema(result, "t1.a", "count");
    }


    public void testExistsCorrelated() throws Throwable {
        CommandResult result;
        TupleLiteral[] expected1 = {
                createTupleFromNum(3),
                createTupleFromNum(4)
        };
        TupleLiteral[] expected2 = {
                createTupleFromNum(1),
                createTupleFromNum(2)
        };
        TupleLiteral[] expected3 = {};

        //----------------------------------
        // Semi-join using EXISTS

        // Use a fully qualified name in the inner query.
        result = server.doCommand(
                "SELECT a FROM test_exists_1 t1 WHERE " +
                        "EXISTS (SELECT b FROM test_exists_2 t2 WHERE t1.a * 10 = b)", true);
        assert checkSizeResults(expected1, result);
        assert checkUnorderedResults(expected1, result);

        // Use an unambiguous column name in the inner query.
        result = server.doCommand(
                "SELECT a FROM test_exists_1 t1 WHERE " +
                        "EXISTS (SELECT b FROM test_exists_2 t2 WHERE a * 10 = b)", true);
        assert checkSizeResults(expected1, result);
        assert checkUnorderedResults(expected1, result);

        //----------------------------------
        // Anti-join using NOT EXISTS

        // Use a fully qualified name in the inner query.
        result = server.doCommand(
                "SELECT a FROM test_exists_1 t1 WHERE " +
                        "NOT EXISTS (SELECT b FROM test_exists_2 t2 WHERE t1.a * 10 = b)", true);
        assert checkSizeResults(expected2, result);
        assert checkUnorderedResults(expected2, result);

        // Use an unambiguous column name in the inner query.
        result = server.doCommand(
                "SELECT a FROM test_exists_1 t1 WHERE " +
                        "NOT EXISTS (SELECT b FROM test_exists_2 t2 WHERE a * 10 = b)", true);
        assert checkSizeResults(expected2, result);
        assert checkUnorderedResults(expected2, result);

        //----------------------------------
        // Semi-join using EXISTS, generating no results

        // Use a fully qualified name in the inner query.
        result = server.doCommand(
                "SELECT a FROM test_exists_1 t1 WHERE " +
                        "EXISTS (SELECT b FROM test_exists_2 t2 WHERE t1.a * -10 = b)", true);
        assert checkSizeResults(expected3, result);
        assert checkUnorderedResults(expected3, result);

        // Use an unambiguous column name in the inner query.
        result = server.doCommand(
                "SELECT a FROM test_exists_1 t1 WHERE " +
                        "EXISTS (SELECT b FROM test_exists_2 t2 WHERE a * -10 = b)", true);
        assert checkSizeResults(expected3, result);
        assert checkUnorderedResults(expected3, result);
    }
}
