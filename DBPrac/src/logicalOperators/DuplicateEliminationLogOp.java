package logicalOperators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Operators.PhysicalPlanBuilder;
import utils.LogicalPlanWriter;

public class DuplicateEliminationLogOp extends LogicalOperator {

	private LogicalOperator childOp;

	public DuplicateEliminationLogOp(LogicalOperator child) {
		childOp= child;
	}

	@Override
	public List<LogicalOperator> getChildren() {
		List<LogicalOperator> children= new ArrayList<LogicalOperator>();
		children.add(childOp);
		return children;
	}

	@Override
	public void accept(PhysicalPlanBuilder planBuilder) {
		planBuilder.visit(this);
	}

	@Override
	public void accept(LogicalPlanWriter lpw) {
		try {
			lpw.visit(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getTableName() {
		return "";
	}
}
