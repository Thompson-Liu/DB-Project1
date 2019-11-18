package logicalOperators;

import java.util.ArrayList;
import java.util.List;

import Operators.PhysicalPlanBuilder;

public class SortLogOp extends LogicalOperator {
	
	private LogicalOperator childOp;
	private List<String> colList;
	
	public SortLogOp(LogicalOperator child, List<String> columns) {
		childOp = child;
		colList = columns;
	}
	
	@Override
	public List<LogicalOperator> getChildren() {
		List<LogicalOperator> children = new ArrayList<LogicalOperator>();
		children.add(childOp);
		return children;
	}
	
	public List<String> getColumns() {
		return colList;
	}
	
	@Override
	public void accept(PhysicalPlanBuilder planBuilder) {
		planBuilder.visit(this);
	}
	
	@Override
	public String getTableName() {
		return "";
	}
}
