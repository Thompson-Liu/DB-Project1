package physicalOperator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dataStructure.DataTable;
import dataStructure.Tuple;
import fileIO.BinaryTupleWriter;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

/** the class for the project operator */
public class ProjectOperator extends Operator {

	private Operator childOp;
	private ArrayList<String> selectColumns;
	private DataTable data;
	private HashMap<String, String> tableAlias;

	/** The constructor for ProjectOperator
	 * 
	 * @param operator The child operator of ProjectOperator, either a ScanOperator or a SelectOperator
	 * @param list The list of items to be projected upon
	 * @param tableAlias The hashmap that maps the name of the table to the alias associated */
	public ProjectOperator(Operator operator, List<SelectItem> list, HashMap<String, String> tableAlias) {
		childOp= operator;
		selectColumns= new ArrayList<String>(list.size());
		this.tableAlias= tableAlias;

		for (SelectItem item : list) {
			// consider the case of Select A.S, B.W, *
			if (item instanceof AllColumns) {
				selectColumns.addAll(operator.schema());
			} else {
				SelectExpressionItem expressItem= (SelectExpressionItem) item;
				String tableColCom= item.toString();
				String[] tableCol= tableColCom.trim().split("\\.");
				String column= tableCol[1];
				String tableName= tableCol[0];

				// if the name has corresponding alias,
				// then change the projection table name to
				if (this.tableAlias.containsKey(tableName)) {
					tableName= this.tableAlias.get(tableName);
				}
				selectColumns.add(tableName + "." + column);
			}
		}

		this.data= new DataTable(operator.getTableName(), selectColumns);
		for (Tuple t: childOp.getData().toArrayList()) {
			data.addData(t);
		}
	}

	/** @return Returns the next tuple read from the data */
	@Override
	public Tuple getNextTuple() {
		Tuple next= null;
		while ((next= childOp.getNextTuple()) != null) {
			Tuple tup= new Tuple();

			for (String item : selectColumns) {

				int index= childOp.schema().indexOf(item.toString());
				tup.addData(next.getData(index));
			}
			data.addData(tup);
			data.setSchema(selectColumns);
			return tup;
		}
		return next;
	}

	/** reset read stream to re-read the data */
	@Override
	public void reset() {
		childOp.reset();
	}

	/** Prints the data read by operator to the PrintStream [ps]
	 * 
	 * @param ps The print stream that the output will be printed to
	 * @param print boolean decides whether the data will actually be printed */
	@Override
	public void dump(BinaryTupleWriter writer) {
		writer.writeTable(getData().toArrayList());
		writer.dump();
		writer.close();
	}

	/** @return the schema of the data table that is read by the operator */
	@Override
	public ArrayList<String> schema() {
		return this.data.getSchema();
	}

	/** @return the table name from where the operator reads the data */
	@Override
	public String getTableName() {
		return data.getTableName();
	}

	/** @return the data read by the operator in DataTable data structure */
	@Override
	public DataTable getData() {
		Tuple t;
		while ((t = getNextTuple()) != null) {
			data.addData(t);
		}
		reset();
		return data;
	}
}
