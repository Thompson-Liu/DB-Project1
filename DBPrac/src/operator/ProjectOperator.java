package operator;

import java.util.ArrayList;
import java.util.List;

import dataStructure.Catalog;
import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class ProjectOperator extends Operator {

	private Operator childOp;
	private ArrayList<SelectItem> selectColumns;

	public ProjectOperator(Operator operator, List<SelectItem> list) {
		childOp= operator;
		selectColumns= new ArrayList<SelectItem>(list);
	}

	@Override
	public Tuple getNextTuple(DataTable table) {
		Tuple next= null;
		while ((next = childOp.getNextTuple(table)) != null) {
			Tuple tup= new Tuple();

			for (SelectItem item : selectColumns) {
				if (item instanceof AllColumns) {
					return next;
				} else {
					SelectExpressionItem expressItem= (SelectExpressionItem) item;

					String select = expressItem.toString();
					String columnName = select.split("\\.")[1];
					int index= table.getSchema().indexOf(columnName);
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
	public DataTable dump(DataTable table) {
		DataTable data = new DataTable("Output", new ArrayList<String>());
		Tuple tup = new Tuple();
		while ((tup = getNextTuple(table)) != null) {
			data.addData(tup.getTuple());
		}
		return data;
	}
}
