package logicalOperators;

import java.util.ArrayList;
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
	
	@Override
	public List<LogicalOperator> getChildren() {
		List<LogicalOperator> children = new ArrayList<LogicalOperator>();
		children.add(childOp);
		return children;
	}
	
	public List<SelectItem> getItems() {
		return projItems;
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
