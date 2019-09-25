package operator;

import java.io.PrintStream;
import java.util.ArrayList;

import dataStructure.DataTable;
import dataStructure.Tuple;

public class DuplicateEliminationOperator extends Operator {

	private DataTable sortedBuffer;
	int ptr;

	/** @param operator operator is the child operator, which has to be a SortOperator because the
	 * precondition requires that the data be sorted first. */
	public DuplicateEliminationOperator(SortOperator operator) {
		// TODO Auto-generated constructor stub
		DataTable tmpTable= operator.getData();
		sortedBuffer= new DataTable("", operator.schema());
		int i= 0;
		while (i < tmpTable.cardinality()) {
			if (i > 0) {
				if (!tmpTable.getRow(i).equals(tmpTable.getRow(i - 1))) {
					sortedBuffer.addData(tmpTable.getRow(i));
				}
				i+= 1;
			} else {
				sortedBuffer.addData(tmpTable.getRow(i));
				i+= 1;
			}
		}
	}

	/** @return the next tuple in the buffer after duplicates are removed */
	@Override
	public Tuple getNextTuple() {
		ptr+= 1;
		if (ptr < sortedBuffer.cardinality()) return new Tuple(sortedBuffer.getRow(ptr));
		return null;
	}

	@Override
	public void dump(PrintStream ps, boolean print) {
		sortedBuffer.printTable(ps);
	}

	/** @return the schema of the data table after duplicates are removed. */
	@Override
	public ArrayList<String> schema() {
		return sortedBuffer.getSchema();
	}

	@Override
	public void reset() {
		ptr= -1;
	}

	/** @return the name of the buffer data table */
	@Override
	public String getTableName() {
		return sortedBuffer.getTableName();
	}

	/** @return the data in the buffer after duplicates are removed */
	@Override
	public DataTable getData() {
		return sortedBuffer;
	}

}
