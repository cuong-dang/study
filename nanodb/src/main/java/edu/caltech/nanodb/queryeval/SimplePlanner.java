package edu.caltech.nanodb.queryeval;


import edu.caltech.nanodb.expressions.Expression;
import edu.caltech.nanodb.plannodes.*;
import edu.caltech.nanodb.queryast.FromClause;
import edu.caltech.nanodb.relations.TableInfo;


public class SimplePlanner extends AbstractPlannerImpl {
    @Override
    PlanNode handleFromWhere(FromClause fromClause, Expression predicate) {
        boolean predicatePushedDown = false;

        PlanNode plan = handleFrom(fromClause, predicate);
        if (plan instanceof FileScanNode) {
            predicatePushedDown = true;
        }
        if (predicate != null && !predicatePushedDown) {
            plan = PlanUtils.addPredicateToPlan(plan, predicate);
        }
        return plan;
    }

    private PlanNode handleFrom(FromClause fromClause, Expression pushDownPredicate) {
        PlanNode fromPlan = null;
        if (fromClause.isBaseTable()) {
            TableInfo tableInfo = storageManager
                    .getTableManager()
                    .openTable(fromClause.getTableName());
            fromPlan = new FileScanNode(tableInfo, pushDownPredicate);
        } else if (fromClause.isDerivedTable()) {
            fromPlan = makeUnpreparedPlan(fromClause.getSelectClause());
        } else if (fromClause.isJoinExpr()) {
            PlanNode leftChild = handleFrom(fromClause.getLeftChild(), null);
            PlanNode rightChild = handleFrom(fromClause.getRightChild(), null);
            fromPlan = new NestedLoopJoinNode(leftChild, rightChild, fromClause.getJoinType(),
                    fromClause.getComputedJoinExpr());
        }
        if (fromClause.isRenamed())
            fromPlan = new RenameNode(fromPlan, fromClause.getResultName());
        return fromPlan;
    }
}
