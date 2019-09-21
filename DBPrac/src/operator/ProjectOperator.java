package operator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class ProjectOperator extends Operator {

	private Operator childOp;
	private ArrayList<String> selectColumns;
	private DataTable data;

	public ProjectOperator(Operator operator, List<SelectItem> list) {
		childOp= operator;
		selectColumns= new ArrayList<String>(list.size());
		for (SelectItem item : list) {
			SelectExpressionItem expressItem= (SelectExpressionItem) item;
			String select= expressItem.toString();
			String columnName= select.split("\\.")[1];
			selectColumns.add(columnName);
		}

		this.data= new DataTable(operator.getTableName(), selectColumns);
//		System.out.println("schema is " + data.getSchema());

	}

	@Override
	public Tuple getNextTuple() {
		Tuple next= null;
		while ((next= childOp.getNextTuple()) != null) {
			Tuple tup= new Tuple();
			ArrayList<String> columns= new ArrayList<String>();

			for (String item : selectColumns) {
//				if (item instanceof AllColumns) {
//					data.addData(next);
//					return next;
//				} else {

				int index= childOp.schema().indexOf(item);
				tup.addData(next.getData(index));
//				}
			}
			data.addData(tup);
			data.setSchema(columns);
			return tup;
		}
		return next;
	}

	@Override
	public void reset() {
		childOp.reset();
	}

	@Override
	public void dump(PrintStream ps, boolean print) {
		Tuple tup;
		while ((tup= getNextTuple()) != null) {

		}
		if (print) {
			data.printTable(ps);
		}
	}

	@Override
	public ArrayList<String> schema() {
		// can't do data.schema(), which returns null
		return selectColumns;
	}

	@Override
	public String getTableName() {
		return data.getTableName();
	}

	@Override
	public DataTable getData() {
		dump(System.out, false);
		return data;
	}
}
