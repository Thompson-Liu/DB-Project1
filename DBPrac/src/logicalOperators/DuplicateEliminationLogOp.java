package logicalOperators;

import Operators.PhysicalPlanBuilder;

public class DuplicateEliminationLogOp extends LogicalOperator {

	private LogicalOperator childOp;
	
	public DuplicateEliminationLogOp(LogicalOperator child) {
		childOp = child;
	}
	
	public LogicalOperator[] getChidren() {
		return new LogicalOperator[] { childOp }; 
	}
	
	public void accept(PhysicalPlanBuilder planBuilder) {
		planBuilder.visit(this);
	}
	
}
