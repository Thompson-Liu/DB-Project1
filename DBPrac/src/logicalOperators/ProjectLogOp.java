package logicalOperators;

import java.util.HashMap;
import java.util.List;

import Operators.PhysicalPlanBuilder;
import net.sf.jsqlparser.statement.select.SelectItem;

public class ProjectLogOp extends LogicalOperator{

	private LogicalOperator childOp;
	private List<SelectItem> projItems;
	
	public ProjectLogOp(LogicalOperator op, List<SelectItem> items) {
		childOp = op;
		projItems = items;
	}
	
	public LogicalOperator[] getChildren() {
		return new LogicalOperator[] { childOp };
	}
	
	public List<SelectItem> getItems() {
		return projItems;
	}
	
	public void accept(PhysicalPlanBuilder planBuilder) {
		planBuilder.visit(this);
	}

}
