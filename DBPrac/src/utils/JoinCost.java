package utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import logicalOperators.JoinLogOp;
import logicalOperators.LogicalOperator;

public class JoinCost {

	public JoinCost() {
		// TODO Auto-generated constructor stub
	}

	public void chooseJoinOrder(JoinLogOp join) {
		LogicalOperator[] joinChildren= join.getChildren();
		List<HashSet<LogicalOperator>> subsets= findSubsets(joinChildren);

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

	private void DPHelper() {

	}

}
