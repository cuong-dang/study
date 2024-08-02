package edu.caltech.nanodb.queryeval;


import edu.caltech.nanodb.expressions.Expression;
import edu.caltech.nanodb.expressions.PredicateUtils;
import edu.caltech.nanodb.plannodes.*;
import edu.caltech.nanodb.queryast.FromClause;
import edu.caltech.nanodb.relations.JoinType;
import edu.caltech.nanodb.relations.TableInfo;

import java.util.*;


/**
 * This planner implementation uses dynamic programming to devise an optimal
 * join strategy for the query.  As always, queries are optimized in units of
 * <tt>SELECT</tt>-<tt>FROM</tt>-<tt>WHERE</tt> subqueries; optimizations
 * don't currently span multiple subqueries.
 */
public class CostBasedJoinPlanner extends AbstractPlannerImpl {
    /**
     * This helper class is used to keep track of one "join component" in the
     * dynamic programming algorithm.  A join component is simply a query plan
     * for joining one or more leaves of the query.
     * <p>
     * In this context, a "leaf" may either be a base table or a subquery in
     * the <tt>FROM</tt>-clause of the query.  However, the planner will
     * attempt to push conjuncts down the plan as far as possible, so even if
     * a leaf is a base table, the plan may be a bit more complex than just a
     * single file-scan.
     */
    private static class JoinComponent {
        /**
         * This is the join plan itself, that joins together all leaves
         * specified in the {@link #leavesUsed} field.
         */
        public PlanNode joinPlan;

        /**
         * This field specifies the collection of leaf-plans that are joined by
         * the plan in this join-component.
         */
        public HashSet<PlanNode> leavesUsed;

        /**
         * This field specifies the collection of all conjuncts use by this join
         * plan.  It allows us to easily determine what join conjuncts still
         * remain to be incorporated into the query.
         */
        public HashSet<Expression> conjunctsUsed;

        /**
         * Constructs a new instance for a <em>leaf node</em>.  It should not
         * be used for join-plans that join together two or more leaves.  This
         * constructor simply adds the leaf-plan into the {@link #leavesUsed}
         * collection.
         *
         * @param leafPlan      the query plan for this leaf of the query.
         * @param conjunctsUsed the set of conjuncts used by the leaf plan.
         *                      This may be an empty set if no conjuncts apply solely to
         *                      this leaf, or it may be nonempty if some conjuncts apply
         *                      solely to this leaf.
         */
        public JoinComponent(PlanNode leafPlan, HashSet<Expression> conjunctsUsed) {
            leavesUsed = new HashSet<>();
            leavesUsed.add(leafPlan);

            joinPlan = leafPlan;

            this.conjunctsUsed = conjunctsUsed;
        }

        /**
         * Constructs a new instance for a <em>non-leaf node</em>.  It should
         * not be used for leaf plans!
         *
         * @param joinPlan      the query plan that joins together all leaves
         *                      specified in the <tt>leavesUsed</tt> argument.
         * @param leavesUsed    the set of two or more leaf plans that are joined
         *                      together by the join plan.
         * @param conjunctsUsed the set of conjuncts used by the join plan.
         *                      Obviously, it is expected that all conjuncts specified here
         *                      can actually be evaluated against the join plan.
         */
        public JoinComponent(PlanNode joinPlan, HashSet<PlanNode> leavesUsed,
                             HashSet<Expression> conjunctsUsed) {
            this.joinPlan = joinPlan;
            this.leavesUsed = leavesUsed;
            this.conjunctsUsed = conjunctsUsed;
        }
    }


    /**
     * Given the top-level {@code FromClause} for a SELECT-FROM-WHERE block,
     * this helper generates an optimal join plan for the {@code FromClause}.
     *
     * @param fromClause     the top-level {@code FromClause} of a
     *                       SELECT-FROM-WHERE block.
     * @param extraConjuncts any extra conjuncts (e.g. from the WHERE clause,
     *                       or HAVING clause)
     * @return a {@code JoinComponent} object that represents the optimal plan
     * corresponding to the FROM-clause
     */
    private JoinComponent makeJoinPlan(FromClause fromClause,
                                       Collection<Expression> extraConjuncts) {

        // These variables receive the leaf-clauses and join conjuncts found
        // from scanning the sub-clauses.  Initially, we put the extra conjuncts
        // into the collection of conjuncts.
        ArrayList<FromClause> leafFromClauses = new ArrayList<>();
        HashSet<Expression> conjuncts = new HashSet<>();

        collectDetails(fromClause, conjuncts, leafFromClauses);

        logger.debug("Making join-plan for " + fromClause);
        logger.debug("    Collected conjuncts:  " + conjuncts);
        logger.debug("    Collected FROM-clauses:  " + leafFromClauses);
        logger.debug("    Extra conjuncts:  " + extraConjuncts);

        if (extraConjuncts != null)
            conjuncts.addAll(extraConjuncts);

        // Make a read-only set of the input conjuncts, to avoid bugs due to
        // unintended side-effects.
        Set<Expression> roConjuncts = Collections.unmodifiableSet(conjuncts);

        // Create a subplan for every single leaf FROM-clause, and prepare the
        // leaf-plan.

        logger.debug("Generating plans for all leaves");
        ArrayList<JoinComponent> leafComponents = generateLeafJoinComponents(
                leafFromClauses, roConjuncts);

        // Print out the results, for debugging purposes.
        if (logger.isDebugEnabled()) {
            for (JoinComponent leaf : leafComponents) {
                logger.debug("    Leaf plan:\n" +
                        PlanNode.printNodeTreeToString(leaf.joinPlan, true));
            }
        }

        // Build up the full query-plan using a dynamic programming approach.

        JoinComponent optimalJoin = generateOptimalJoin(leafComponents, roConjuncts);

        PlanNode plan = optimalJoin.joinPlan;
        logger.info("Optimal join plan generated:\n" +
                PlanNode.printNodeTreeToString(plan, true));

        return optimalJoin;
    }


    /**
     * This helper method pulls the essential details for join optimization
     * out of a <tt>FROM</tt> clause.
     *
     * @param fromClause      the from-clause to collect details from
     * @param conjuncts       the collection to add all conjuncts to
     * @param leafFromClauses the collection to add all leaf from-clauses to
     */
    private void collectDetails(FromClause fromClause,
                                HashSet<Expression> conjuncts, ArrayList<FromClause> leafFromClauses) {
        if (fromClause.isBaseTable() || fromClause.isDerivedTable() || fromClause.isOuterJoin()) {
            leafFromClauses.add(fromClause);
            return;
        }
        assert fromClause.isJoinExpr() && !fromClause.isOuterJoin();
        PredicateUtils.collectConjuncts(fromClause.getComputedJoinExpr(), conjuncts);
        collectDetails(fromClause.getLeftChild(), conjuncts, leafFromClauses);
        collectDetails(fromClause.getRightChild(), conjuncts, leafFromClauses);
    }


    /**
     * This helper method performs the first step of the dynamic programming
     * process to generate an optimal join plan, by generating a plan for every
     * leaf from-clause identified from analyzing the query.  Leaf plans are
     * usually very simple; they are built either from base-tables or
     * <tt>SELECT</tt> subqueries.  The most complex detail is that any
     * conjuncts in the query that can be evaluated solely against a particular
     * leaf plan-node will be associated with the plan node.  <em>This is a
     * heuristic</em> that usually produces good plans (and certainly will for
     * the current state of the database), but could easily interfere with
     * indexes or other plan optimizations.
     *
     * @param leafFromClauses the collection of from-clauses found in the query
     * @param conjuncts       the collection of conjuncts that can be applied at this
     *                        level
     * @return a collection of {@link JoinComponent} object containing the plans
     * and other details for each leaf from-clause
     */
    private ArrayList<JoinComponent> generateLeafJoinComponents(
            Collection<FromClause> leafFromClauses, Collection<Expression> conjuncts) {

        // Create a subplan for every single leaf FROM-clause, and prepare the
        // leaf-plan.
        ArrayList<JoinComponent> leafComponents = new ArrayList<>();
        for (FromClause leafClause : leafFromClauses) {
            HashSet<Expression> leafConjuncts = new HashSet<>();

            PlanNode leafPlan = makeLeafPlan(leafClause, conjuncts, leafConjuncts);

            JoinComponent leaf = new JoinComponent(leafPlan, leafConjuncts);
            leafComponents.add(leaf);
        }

        return leafComponents;
    }


    /**
     * Constructs a plan tree for evaluating the specified from-clause.
     *
     * @param fromClause    the select nodes that need to be joined.
     * @param conjuncts     additional conjuncts that can be applied when
     *                      constructing the from-clause plan.
     * @param leafConjuncts this is an output-parameter.  Any conjuncts
     *                      applied in this plan from the <tt>conjuncts</tt> collection
     *                      should be added to this out-param.
     * @return a plan tree for evaluating the specified from-clause
     * @throws IllegalArgumentException if the specified from-clause is a join
     *                                  expression that isn't an outer join, or has some other
     *                                  unrecognized type.
     */
    private PlanNode makeLeafPlan(FromClause fromClause,
                                  Collection<Expression> conjuncts, HashSet<Expression> leafConjuncts) {
        PlanNode plan;

        if (fromClause.isBaseTable() || fromClause.isDerivedTable()) {
            if (fromClause.isBaseTable()) {
                TableInfo tableInfo = storageManager
                        .getTableManager()
                        .openTable(fromClause.getTableName());
                plan = new FileScanNode(tableInfo, null);
            } else {
                plan = makeUnpreparedPlan(fromClause.getSelectClause());
            }
            if (fromClause.isRenamed()) {
                plan = new RenameNode(plan, fromClause.getResultName());
            }
            plan.prepare();
            plan = pushConjuncts(plan, conjuncts, leafConjuncts);
        } else {
            assert fromClause.isOuterJoin();
            PlanNode left = handleFromWhere(fromClause.getLeftChild(), null);
            PlanNode right = handleFromWhere(fromClause.getRightChild(), null);
            if (fromClause.hasOuterJoinOnLeft() && !fromClause.hasOuterJoinOnRight()) {
                HashSet<Expression> conjunctsWithJoinExpr = new HashSet<>(conjuncts);
                conjunctsWithJoinExpr.add(fromClause.getComputedJoinExpr());
                left.prepare();
                left = pushConjuncts(left, conjunctsWithJoinExpr, leafConjuncts);
            }
            plan = new NestedLoopJoinNode(left, right, fromClause.getJoinType(), fromClause.getComputedJoinExpr());
        }
        return plan;
    }

    private PlanNode pushConjuncts(PlanNode plan, Collection<Expression> conjuncts,
                                   HashSet<Expression> appliedConjuncts) {
        logger.debug("Pushing conjuncts for plan: " + plan + "; conjuncts: " + conjuncts);
        PredicateUtils.findExprsUsingSchemas(conjuncts, false, appliedConjuncts, plan.getSchema());
        logger.debug("Applied conjuncts: " + appliedConjuncts);
        if (!appliedConjuncts.isEmpty()) {
            plan = PlanUtils.addPredicateToPlan(plan, PredicateUtils.makePredicate(appliedConjuncts));
            plan.prepare();
        }
        return plan;
    }


    /**
     * This helper method builds up a full join-plan using a dynamic programming
     * approach.  The implementation maintains a collection of optimal
     * intermediate plans that join <em>n</em> of the leaf nodes, each with its
     * own associated cost, and then uses that collection to generate a new
     * collection of optimal intermediate plans that join <em>n+1</em> of the
     * leaf nodes.  This process completes when all leaf plans are joined
     * together; there will be <em>one</em> plan, and it will be the optimal
     * join plan (as far as our limited estimates can determine, anyway).
     *
     * @param leafComponents the collection of leaf join-components, generated
     *                       by the {@link #generateLeafJoinComponents} method.
     * @param conjuncts      the collection of all conjuncts found in the query
     * @return a single {@link JoinComponent} object that joins all leaf
     * components together in an optimal way.
     */
    private JoinComponent generateOptimalJoin(
            ArrayList<JoinComponent> leafComponents, Set<Expression> conjuncts) {

        // This object maps a collection of leaf-plans (represented as a
        // hash-set) to the optimal join-plan for that collection of leaf plans.
        //
        // This collection starts out only containing the leaf plans themselves,
        // and on each iteration of the loop below, join-plans are grown by one
        // leaf.  For example:
        //   * In the first iteration, all plans joining 2 leaves are created.
        //   * In the second iteration, all plans joining 3 leaves are created.
        //   * etc.
        // At the end, the collection will contain ONE entry, which is the
        // optimal way to join all N leaves.  Go Go Gadget Dynamic Programming!
        HashMap<HashSet<PlanNode>, JoinComponent> joinPlans = new HashMap<>();

        // Initially populate joinPlans with just the N leaf plans.
        for (JoinComponent leaf : leafComponents) {
            joinPlans.put(leaf.leavesUsed, leaf);
        }

        while (joinPlans.size() > 1) {
            logger.debug("Current set of join-plans has " + joinPlans.size() +
                    " plans in it.");

            // This is the set of "next plans" we will generate.  Plans only
            // get stored if they are the first plan that joins together the
            // specified leaves, or if they are better than the current plan.
            HashMap<HashSet<PlanNode>, JoinComponent> nextJoinPlans = new HashMap<>();

            for (JoinComponent joinPlanN : joinPlans.values()) {
                for (JoinComponent leaf : leafComponents) {
                    if (joinPlanN.leavesUsed.contains(leaf.joinPlan)) {
                        continue;
                    }
                    /* Get applicable conjuncts */
                    HashSet<Expression> conjunctsNP1 = new HashSet<>(joinPlanN.conjunctsUsed);
                    conjunctsNP1.addAll(leaf.conjunctsUsed);
                    HashSet<Expression> unusedConjuncts = new HashSet<>(conjuncts);
                    unusedConjuncts.removeAll(conjunctsNP1);
                    HashSet<Expression> applicableConjuncts = new HashSet<>();
                    assert joinPlanN.joinPlan.getSchema() != null;
                    assert leaf.joinPlan.getSchema() != null;
                    PredicateUtils.findExprsUsingSchemas(unusedConjuncts, false, applicableConjuncts,
                            joinPlanN.joinPlan.getSchema(), leaf.joinPlan.getSchema());
                    /* Create an N+1 join plan */
                    HashSet<PlanNode> leavesNP1 = new HashSet<>(joinPlanN.leavesUsed);
                    leavesNP1.add(leaf.joinPlan);
                    conjunctsNP1.addAll(applicableConjuncts);
                    PlanNode joinNodeNP1 = new NestedLoopJoinNode(joinPlanN.joinPlan, leaf.joinPlan, JoinType.INNER,
                            PredicateUtils.makePredicate(applicableConjuncts));
                    joinNodeNP1.prepare();
                    JoinComponent joinPlanNP1 = new JoinComponent(joinNodeNP1, leavesNP1, conjunctsNP1);
                    if (!nextJoinPlans.containsKey(leavesNP1) ||
                            nextJoinPlans.get(leavesNP1).joinPlan.getCost().cpuCost > joinNodeNP1.getCost().cpuCost) {
                        nextJoinPlans.put(leavesNP1, joinPlanNP1);
                    }
                }
            }

            // Now that we have generated all plans joining N leaves, time to
            // create all plans joining N + 1 leaves.
            joinPlans = nextJoinPlans;
        }

        // At this point, the set of join plans should only contain one plan,
        // and it should be the optimal plan.

        assert joinPlans.size() == 1 : "There can be only one optimal join plan!";
        return joinPlans.values().iterator().next();
    }

    @Override
    PlanNode handleFromWhere(FromClause fromClause, Expression predicate) {
        HashSet<Expression> conjuncts = new HashSet<>();
        PredicateUtils.collectConjuncts(predicate, conjuncts);
        JoinComponent optimalJoinPlan = makeJoinPlan(fromClause, conjuncts);
        PlanNode plan = optimalJoinPlan.joinPlan;
        conjuncts.removeAll(optimalJoinPlan.conjunctsUsed);
        if (!conjuncts.isEmpty()) {
            plan = PlanUtils.addPredicateToPlan(plan, PredicateUtils.makePredicate(conjuncts));
        }
        return plan;
    }
}
