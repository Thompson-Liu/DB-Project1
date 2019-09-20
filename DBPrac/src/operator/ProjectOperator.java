package operator;

import java.io.PrintStream;
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
	private DataTable data;

	public ProjectOperator(Operator operator, List<SelectItem> list) {
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
					data.addData(next);
					return next;
				} else {
					SelectExpressionItem expressItem= (SelectExpressionItem) item;

					String select = expressItem.toString();
					String columnName = select.split("\\.")[1];
					
					int index= childOp.schema().indexOf(columnName);
					tup.addData(next.getData(index));
				}
			}
			data.addData(tup);
			return tup;
		}
		return next;
	}

	@Override
	public void reset() {
		childOp.reset();
	}

	@Override
	public void dump(PrintStream ps) {
		data.printTable(ps);
	}
	
	@Override
	public ArrayList<String> schema() {
		return data.getSchema();
	}
}
