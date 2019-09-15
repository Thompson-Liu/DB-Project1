package operator;

import java.util.ArrayList;
import java.util.List;

import dataStructure.Catalog;
import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class ProjectOperator extends ScanOperator {

	private Operator childOp;
	private ArrayList<SelectItem> selectColumns;
	private String tableName;

	public ProjectOperator(Operator operator, DataTable table, List<SelectItem> list) {
		super(name);
		tableName= name;
		childOp= operator;
		selectColumns= new ArrayList<SelectItem>(list);
	}

	@Override
	public Tuple getNextTuple() {
		Tuple next= null;
		while ((next = childOp.getNextTuple()) != null) {
			Tuple tup= new Tuple();

			for (SelectItem item : selectColumns) {
				if (item instanceof AllColumns) {
					return next;
				} else {
					SelectExpressionItem expressItem= (SelectExpressionItem) item;

					String select = expressItem.toString();
					String columnName = select.split("\\.")[1];
					int index= schema.indexOf(columnName);
					tup.addData(next.getData(index));
				}
			}
			return tup;
		}
		return next;
	}

	@Override
	public void reset() {
		childOp.reset();
	}

	@Override
	public DataTable dump() {
		return super.dump();
	}
}
