package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import dataStructure.BlueBox;
import dataStructure.Catalog;
import dataStructure.OpStats;
import logicalOperators.JoinLogOp;
import logicalOperators.LogicalOperator;
import logicalOperators.SelectLogOp;
import net.sf.jsqlparser.expression.Expression;

public class JoinCost {
	private Catalog cat;
	private HashMap<String, LogicalOperator> bestSubPlan;            // rightMost Table   and the corresponding Logical Operator
	private HashMap<HashSet<LogicalOperator>, Integer> dpTable;      //v(R)  number of tuples resulting in a operation
	private HashMap<String, HashMap<String,Integer>> columnStats;   // v(R,A)   for a column of relation

	public JoinCost() {
		dpTable= new HashMap<HashSet<LogicalOperator>, Integer>();
		cat=Catalog.getInstance();
	}

	/** Compute cost of join in a bottom-up fashion using Dynamic Programming. */
	public void findOptimalJoinOrder(JoinLogOp join) {

		LogicalOperator[] joinChildren= join.getChildren();
		List<HashSet<LogicalOperator>> subsets= findSubsets(joinChildren);
		for (int i= 0; i < subsets.size(); i++ ) {
			HashSet<LogicalOperator> currSet= subsets.get(i);

			HashSet<LogicalOperator> subsetCopy= new HashSet<LogicalOperator>(currSet);
			Integer bestCost= Integer.MAX_VALUE;

			for (LogicalOperator logOp : currSet) {
				subsetCopy.remove(logOp);
				//get the previous intermediate number of tuples
				Integer previousTuples= dpTable.get(subsetCopy);
				// Compute join query cost here, using previous cost

				subsetCopy.add(logOp);

			}
			dpTable.put(currSet, bestCost);
		}

	}

	/** Assumes array doesn't contain duplicates. Used backtracking to find all possible non-empty
	 * subsets of an array of logical operators.
	 * 
	 * @return a list of all the non-empty subsets of arr. */
	private List<HashSet<LogicalOperator>> findSubsets(LogicalOperator[] arr) {
		List<HashSet<LogicalOperator>> rst= new ArrayList<>();
		backtrack(rst, new ArrayList<LogicalOperator>(), arr, 0);
		return rst;
	}

	private void backtrack(List<HashSet<LogicalOperator>> list, List<LogicalOperator> tempList, LogicalOperator[] arr,
			int start) {
		if (tempList.size() > 0) {
			list.add(new HashSet<LogicalOperator>(tempList));
		}
		for (int i= start; i < arr.length; i++ ) {
			tempList.add(arr[i]);
			backtrack(list, tempList, arr, i + 1);
			tempList.remove(tempList.size() - 1);
		}
	}

	//	private void DPHelper(HashSet<LogicalOperator> combo) {
	//		if (!dpTable.containsKey(combo)) {
	//			
	//		}
	//		else {
	//			
	//		}
	//	}

	/** helper method to set the V of baseTable
	 *  update tableCols and tabelColV if the operator is only scan
	 * @param log
	 */
	private void baseTableVupdate(LogicalOperator log) {
		Catalog catalog = Catalog.getInstance();
		String tableName = log.getTableName();
		int numTuples = catalog.getTupleNums(tableName);
		HashSet<LogicalOperator> cur = new HashSet<LogicalOperator>();
		cur.add(log);
		// fill the basecase hashMap
		ArrayList<String> schema = catalog.getSchema(tableName);
		for(String col:schema) {
			int range = catalog.getColRange(tableName, col)[1]-catalog.getColRange(tableName, col)[0]+1;
			this.columnStats.get(tableName).put(col, Math.min(range,numTuples));
		}
		this.dpTable.put(cur, numTuples);
	}

	/** helper method to set the V of selection table
	 * @param log
	 */
	private void selectionVupdate(LogicalOperator log) {
		Catalog catalog = Catalog.getInstance();
		String tableName = log.getTableName();
		ArrayList<String> schema = catalog.getSchema(tableName);
		int prevTuples = this.dpTable.get(log);
		int newNum=prevTuples;
		//before choosing scan or after
		double reductionFactor =1;
		for(String col:schema) {
			if(col is in selection to reduce) {  // check if col is in expr {R.A=3 or R.A<4} with low and high bound
				int orignal_high = catalog.getColRange(tableName, col)[1];
				int original_low=catalog.getColRange(tableName, col)[0]+1;
				reductionFactor = reductionFactor* (high-low)/(original_high-original_low);
				this.columnStats.get(tableName).put(col, high-low);
			}
		}
		newNum = (int) (reductionFactor*newNum);
		newNum = (newNum==0) ?  1:newNum;
		// update the column hashmap if table-tuple is smaller
		for(String col:schema) {
			int colRange = this.columnStats.get(tableName).get(col);
			this.columnStats.get(tableName).put(col, Math.min(colRange,newNum));
		}
		this.dpTable.put(log,newNum);
	}

	private void joinVupdate(LogicalOperator left, LogicalOperator right) {
		String leftTableName = left.getTableName();
		String rightTableName = right.getTableName();
		ArrayList<ArrayList<String>> conditionSets; //(relating to left and right)  e.g.[["S.A=B.C, B.C=D.E"],["S.Y=B.X"]]
		Integer reductionV = 1;
		// if there exists a set of bluebox relating to left and right
		for(ArrayList<String> conditionCols :conditionSets) {
			int v=Integer.MAX_VALUE;
			// get the minimum column V
			for(String col:conditionCols) {
				String relationName = col.split("//.")[0];
				String columnName = col.split("//.")[1];
				int size =this.columnStats.get(relationName).get(columnName);
				v = Math.min(size, v);
			}
			reductionV= reductionV*v;
			// update v for attributes to the min(S.A,R.B)
			for(String col:conditionCols) {
				String relationName = col.split("//.")[0];
				String columnName = col.split("//.")[1];
				this.columnStats.get(relationName).put(columnName,v);
			}
		}


	}

	private int computeIntermediate(left, right) {
		// compute intermediate join size
		int joinSize = (int) leftRelation.size()*rightRelation.size()/(reductionV);
		
	}

}
