package operator;

import java.util.ArrayList;
import java.util.List;

import dataStructure.Catalog;
import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

public class ProjectOperator extends ScanOperator {
	
	private PlainSelect plainSelect;
	private Operator childOp;
	private ArrayList<SelectItem> selectColumns;
	private String tableName; 
	
	public ProjectOperator(PlainSelect ps, Operator operator, String name, List<SelectItem> list) {
		super(name);
		tableName = name;
		plainSelect = ps;
		childOp = operator;
		selectColumns = new ArrayList<SelectItem>(list);
	}
	
	public Tuple getNextTuple(){
		Tuple next = childOp.getNextTuple();
		Tuple tup = new Tuple();
		
		Catalog catalog = Catalog.getInstance();
		ArrayList<String> schema = catalog.getSchema(tableName);
		
		for (SelectItem item: selectColumns) {
			if (item instanceof AllColumns) {
				return next;
			} else {
				SelectExpressionItem expressItem = (SelectExpressionItem) item;
				int index = schema.indexOf(expressItem.toString());
				tup.addData(next.getData(index));
			}
		}
		SelectOperator selectOp = childOp;
		selectOp.removeLastTuple();
		selectOp.addNewTuple(tup);
		return tup;
	}
	
	public void reset() {
		childOp.reset();
	}
	
	public DataTable dump() {
		return childOp.dump();
	}
}
