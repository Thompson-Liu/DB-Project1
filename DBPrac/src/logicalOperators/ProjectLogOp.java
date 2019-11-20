package logicalOperators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Operators.PhysicalPlanBuilder;
import net.sf.jsqlparser.statement.select.SelectItem;
import utils.LogicalPlanWriter;

public class ProjectLogOp extends LogicalOperator {

	private LogicalOperator childOp;
	private List<SelectItem> projItems;

	public ProjectLogOp(LogicalOperator op, List<SelectItem> items) {
		childOp= op;
		projItems= items;
	}

	@Override
	public List<LogicalOperator> getChildren() {
		List<LogicalOperator> children= new ArrayList<LogicalOperator>();
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
