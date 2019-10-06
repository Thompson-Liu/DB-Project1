package logicalOperators;

import java.util.List;

import Operators.PhysicalPlanBuilder;

public class SortLogOp extends LogicalOperator {
	
	private LogicalOperator childOp;
	private List<String> colList;
	
	public SortLogOp(LogicalOperator child, List<String> columns) {
		childOp = child;
		colList = columns;
	}
	
	public LogicalOperator[] getChildren() {
		return new LogicalOperator[] { childOp };
	}
	
	public List<String> getColumns() {
		return colList;
	}
	
	public void accept(PhysicalPlanBuilder planBuilder) {
		planBuilder.visit(this);
	}
}
