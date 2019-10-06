package logicalOperators;

import Operators.PhysicalPlanBuilder;

/** the abstract class that all operator classes extend. */
public abstract class LogicalOperator {

	public LogicalOperator[] getChildren() {
		return null;
	}

	public void accept(PhysicalPlanBuilder physicalPlanBuilder) {
		// TODO Auto-generated method stub
	}

}