package physicalOperator;

import java.util.ArrayList;

import dataStructure.DataTable;
import dataStructure.Tuple;
import fileIO.TupleWriter;

/** the class for the duplicate elimination operator that removes duplicates tuples from the data
 * its child operator generates. */
public class DuplicateEliminationOperator extends Operator {
	private Tuple prevTuple;
	private Tuple currTuple;
	private ExternalSortOperator exSortOp;

	/** @param operator operator is the child operator, which has to be a SortOperator because the
	 * precondition requires that the data be sorted first. */
	public DuplicateEliminationOperator(ExternalSortOperator operator) {
		// TODO Auto-generated constructor stub
		exSortOp= operator;
		Tuple tmp= operator.getNextTuple();
		prevTuple= tmp;
		currTuple= tmp;
	}

	/** @return the next tuple in the buffer after duplicates are removed */
	@Override
	public Tuple getNextTuple() {
		while (currTuple.equals(prevTuple)) {
			currTuple= exSortOp.getNextTuple();
		}
		prevTuple= currTuple;
		return currTuple;
	}

	@Override
	public void dump(TupleWriter writer) {
		Tuple tup= getNextTuple();
		while (tup != null) {
			writer.addNextTuple(tup);
			tup= getNextTuple();
		}
		writer.dump();
		writer.close();
	}

	/** @return the schema of the data table after duplicates are removed. */
	@Override
	public ArrayList<String> schema() {
		return exSortOp.schema();
	}

	@Override
	public void reset() {
		exSortOp.reset();
	}

	/** @return the name of the buffer data table */
	@Override
	public String getTableName() {
		return exSortOp.getTableName();
	}

	/** @return the data in the buffer after duplicates are removed */
	@Override
	public DataTable getData() {
		return null;
	}

}
