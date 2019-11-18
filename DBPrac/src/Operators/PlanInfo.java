/**
 * Helper structure for join optimizer that store subplan's intermediate result and optimal constructed Operator
 */
package Operators;

import java.util.ArrayList;
import java.util.HashMap;

import logicalOperators.LogicalOperator;
import physicalOperator.Operator;

public class PlanInfo {
	
//	private Operator operator;
	private String firstTableName;
	private ArrayList<LogicalOperator> logOps;
	private int cost;                      // total cost for this [operator] tree plan
	private int totalTuples;                    // the total number of tuples of the relation resulted from this plan
	private HashMap<String,Integer> columnStats;   // v(R.A)   for a column of relation
	
	public PlanInfo(ArrayList<LogicalOperator> logOps) {
		this.logOps=logOps;
	}
	
	
	public void setCost(int c) {
		this.cost =c;
	}
	
	public void setTotalTuples(int totalTuples) {
		totalTuples= (totalTuples==0)? 1: totalTuples;        // make sure this is not 0
		this.totalTuples=totalTuples;
	}
	
	/**
	 * 
	 * @return  the number of logical operators in this PlanInfo
	 */
	public int getNumOps() {
		return logOps.size();
	}
	
	/**
	 *  add the v value to the plan Info
	 * @param tableName  Alias Name
	 * @param colName   column of the v value
	 * @param v        the v value
	 */
	public void addColV(String tableName, String colName,int v) {
		String tabCol = tableName+"."+colName;
		// check to make sure keep the smaller v value
		if(columnStats.containsKey(tabCol)) {
			int preV= columnStats.get(tabCol);
			v = Math.min(v, preV);
		}
		this.columnStats.put(tabCol, v);
	}
	
	public int getCost() {
		return cost;
	}
	
	public ArrayList<LogicalOperator> getOpsCopy() {
		ArrayList<LogicalOperator> temp = new ArrayList<LogicalOperator>();
		temp.addAll(this.logOps);
		return temp;
	}
	
	public int getTotalTuples() {
		return this.totalTuples;
	}
	
	/**
	 *    helper for getFirstTableName  ,  detail check below function
	 * @param firstTable the name of the first table
	 */
	public void setFirstTableName(String firstTable) {
		this.firstTableName=firstTable;
	}
	
	/**
	 *  This function is used to get the table name of the TopMost Table
	 * @return
	 */
	public String getFirstTableName() {
		return firstTableName;
	}
	
	/**
	 * 
	 * @return a copy of column statistics for this subPlan
	 */
	public HashMap<String, Integer> copyColStas(){
		HashMap<String, Integer> copy = new HashMap<String, Integer>();
		for(String tabCol : columnStats.keySet()) {
			copy.put(tabCol, columnStats.get(tabCol));
		}
		return copy;
	}
	
	/**
	 * 
	 * @param tableName  Alias name of the table
	 * @param colName  
	 * @return          the v value of this column of tableName
	 */
	public Integer getColV(String tableName, String colName) {
		String tabCol = tableName+"."+colName;
		return columnStats.get(tabCol);
	}

}
