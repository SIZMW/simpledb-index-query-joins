package simpledb.planner;

import java.util.logging.Level;

import simpledb.tx.Transaction;
import simpledb.query.*;
import simpledb.parse.*;
import simpledb.server.SimpleDB;
import simpledb.materialize.MergeJoinPlan;
import java.util.*;

/**
 * CS 4432 Project 2
 * 
 * Always uses MergeJoinPlan.
 */
public class SortQueryPlanner implements QueryPlanner {
    /**
     * Creates a query plan as follows.  It first takes the SmartMergeJoin
     * of all fields. It then selects on the predicate;
     * and finally it projects on the field list. 
     */
    public Plan createPlan(QueryData data, Transaction tx) {
        //Step 1: Create a plan for each mentioned table or view
        List<TablePlan> tablePlans = new ArrayList<TablePlan>();
        for (String tblname : data.tables()) {
            tablePlans.add(new TablePlan(tblname, tx));
        }

        Plan currentPlan = tablePlans.remove(0);

        // Add plans to the join order
        for (TablePlan tp: tablePlans) {
            Plan temp = makeMergeJoinPlan(currentPlan, tp, data.pred(), tx);
            if (temp == null) {
                temp = makeProductPlan(currentPlan, tp);
            }
            currentPlan = temp;
        }

        currentPlan = new SelectPlan(currentPlan, data.pred());

        //Step 4: Project on the field names
        Plan projectPlan = new ProjectPlan(currentPlan, data.fields());
        return projectPlan;
    }

    private Plan makeMergeJoinPlan(Plan p1, TablePlan p2, Predicate pred, Transaction trans) {
        for (String field1: p1.schema().fields()) {
            String field2 = pred.equatesWithField(field1);
            if (field2 != null && p2.schema().hasField(field2)) {
                SimpleDB.getLogger().log(Level.INFO, "Creating new MergeJoinPlan");
                return new MergeJoinPlan(p1, p2, field1, field2, trans);
            }
        }
        return null;
    }

    private Plan makeProductPlan(Plan p1, Plan p2) {
        return new ProductPlan(p1, p2);
    }
}
