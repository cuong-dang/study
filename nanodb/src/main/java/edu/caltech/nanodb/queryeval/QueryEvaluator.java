package edu.caltech.nanodb.queryeval;


import edu.caltech.nanodb.plannodes.PlanNode;

import edu.caltech.nanodb.relations.Schema;
import edu.caltech.nanodb.relations.Tuple;


public class QueryEvaluator {

    /**
     * Executes the specified query plan, and feeds the results to the specified
     * tuple processor.
     *
     * @param plan the query plan to execute
     *
     * @param processor the tuple-processor to receive the results
     *
     * @return An object containing statistics about the plan evaluation.
     */
    public static EvalStats executePlan(PlanNode plan, TupleProcessor processor) {

        // Execute the plan, and record some basic statistics as we go.

        long startTime = System.nanoTime();

        Schema resultSchema = plan.getSchema();
        processor.setSchema(resultSchema);

        plan.initialize();

        int rowsProduced = 0;
        try {
            Tuple tuple;
            while (true) {
                // Get the next tuple.  If there aren't anymore, we're done!
                tuple = plan.getNextTuple();
                if (tuple == null)
                    break;

                rowsProduced++;

                // Do whatever we're supposed to do with the tuple
                processor.process(tuple);
                tuple.unpin();
            }
        }
        finally {
            plan.cleanUp();
        }

        processor.finish();

        long elapsedTimeNanos = System.nanoTime() - startTime;

        // Return the basic statistics we gathered.
        return new EvalStats(rowsProduced, elapsedTimeNanos);
    }
}

