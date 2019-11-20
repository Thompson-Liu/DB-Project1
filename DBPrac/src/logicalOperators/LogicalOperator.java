package logicalOperators;

import java.util.List;

import Operators.PhysicalPlanBuilder;

/** the abstract class that all operator classes extend. */
public abstract class LogicalOperator {

	public List<LogicalOperator> getChildren() {
		return null;
	}

	public void accept(PhysicalPlanBuilder physicalPlanBuilder) {
		// TODO Auto-generated method stub
	}

	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}
}