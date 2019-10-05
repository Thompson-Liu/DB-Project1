package logicalOperators;

import java.util.HashMap;
import java.util.List;

import net.sf.jsqlparser.statement.select.SelectItem;

public class ProjectLogOp extends LogicalOperator{

	private LogicalOperator childOp;
	private HashMap<String, String> tableAlias;
	private List<SelectItem> projItems;
	
	
	public ProjectLogOp(LogicalOperator op, List<SelectItem> items, HashMap<String, String> alias) {
		childOp = op;
		projItems = items;
		tableAlias = alias;
	}
	
	public LogicalOperator[] getChildren() {
		return new LogicalOperator[] { childOp };
	}
	
	public List<SelectItem> getItems() {
		return projItems;
	}
	
	public HashMap<String, String>getAlias() {
		return tableAlias;
	}

}
