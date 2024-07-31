package edu.caltech.test.nanodb.sql;

import edu.caltech.nanodb.expressions.TupleLiteral;
import edu.caltech.nanodb.server.CommandResult;
import org.testng.annotations.Test;

@Test(groups={"sql", "hw2"})
public class TestOrderBy extends SqlTestCase {
    public TestOrderBy() {
        super("setup_testGroupingAndAggregation");
    }

    public void test() throws Throwable {
        CommandResult result;
        result = server.doCommand(
                "SELECT branch_name, MAX(balance) FROM test_group_aggregate_b GROUP BY branch_name ORDER BY branch_name", true);
        TupleLiteral[] expected = {
                new TupleLiteral( "Belldale" , 67000 ),
                new TupleLiteral( "Bretton" , 91000 ),
                new TupleLiteral( "Brighton" , 24000 ),
                new TupleLiteral( "Central" , 2000 ),
                new TupleLiteral( "Deer Park" , 19000 ),
                new TupleLiteral( "Downtown" , 200 ),
                new TupleLiteral( "Greenfield" , 92000 ),
                new TupleLiteral( "Marks" , 69000 ),
                new TupleLiteral( "Mianus" , 74000 ),
                new TupleLiteral( "North Town" , 37000 ),
                new TupleLiteral( "Perryridge" , 82000 ),
                new TupleLiteral( "Pownal" , 91000 ),
                new TupleLiteral( "Redwood" , 8200 ),
                new TupleLiteral( "Rock Ridge" , 50000 ),
                new TupleLiteral( "Round Hill" , 34000 ),
                new TupleLiteral( "Stonewell" , 66000 )
        };
        assert checkSizeResults(expected, result);
        assert checkOrderedResults(expected, result);
    }
}