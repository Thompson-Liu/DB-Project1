package logicalOperators;

import java.util.ArrayList;
import java.util.List;

import Operators.PhysicalPlanBuilder;

public class DuplicateEliminationLogOp extends LogicalOperator {

	private LogicalOperator childOp;
	
	public DuplicateEliminationLogOp(LogicalOperator child) {
		childOp = child;
	}
	
	@Override
	public List<LogicalOperator> getChildren() {
		List<LogicalOperator> children = new ArrayList<LogicalOperator>();
		children.add(childOp);
		return children;
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
