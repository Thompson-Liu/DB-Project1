package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import logicalOperators.JoinLogOp;
import logicalOperators.LogicalOperator;

public class JoinCost {
	private HashMap<HashSet<LogicalOperator>, Integer> dpTable;

	public JoinCost() {
		dpTable= new HashMap<HashSet<LogicalOperator>, Integer>();
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
				Integer previousCost= dpTable.get(subsetCopy);
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
//
//	}

}
