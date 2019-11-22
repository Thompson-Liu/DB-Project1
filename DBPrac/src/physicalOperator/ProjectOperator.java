package physicalOperator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dataStructure.Tuple;
import fileIO.TupleWriter;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.SelectItem;
import utils.PhysicalPlanWriter;

/** the class for the project operator */
public class ProjectOperator extends Operator {

	private Operator childOp;
	private ArrayList<String> selectColumns;
	private String tableName;

	/** The constructor for ProjectOperator
	 * 
	 * @param operator The child operator of ProjectOperator, either a ScanOperator or a SelectOperator
	 * @param list The list of items to be projected upon
	 * @param tableAlias The hashmap that maps the name of the table to the alias associated */
	public ProjectOperator(Operator operator, List<SelectItem> list) {
		childOp= operator;
		selectColumns= new ArrayList<String>(list.size());
		this.tableName= operator.getTableName();

		for (SelectItem item : list) {
			// consider the case of Select A.S, B.W, *
			if (item instanceof AllColumns) {
				selectColumns.addAll(operator.schema());
			} else {
				String tableColCom= item.toString();
				String[] tableCol= tableColCom.trim().split("\\.");
				String column= tableCol[1];
				String tableName= tableCol[0];
				selectColumns.add(tableName + "." + column);
			}
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
	public void dump(TupleWriter writer) {
		reset();
		Tuple tup;
		while ((tup= getNextTuple()) != null) {
			writer.addNextTuple(tup);
		}
		writer.dump();
		writer.close();
	}

	/** @return the schema of the data table that is read by the operator */
	@Override
	public ArrayList<String> schema() {
		return this.selectColumns;
	}

	/** @return the table name from where the operator reads the data */
	@Override
	public String getTableName() {
		return this.tableName;
	}

	public Operator getChild() {
		return childOp;
	}

	public ArrayList<String> getSelectCols() {
		return selectColumns;
	}

	@Override
	public void accept(PhysicalPlanWriter ppw) {
		try {
			ppw.visit(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
