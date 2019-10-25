package physicalOperator;

import java.util.ArrayList;

import dataStructure.Tuple;
import fileIO.TupleWriter;

/** the class for the duplicate elimination operator that removes duplicates tuples from the data
 * its child operator generates. */
public class DuplicateEliminationOperator extends Operator {
	private Tuple prevTuple;
	private Tuple currTuple;
	private Operator sortOp;

	/** @param operator operator is the child operator, which has to be a SortOperator because the
	 * precondition requires that the data be sorted first. */
	public DuplicateEliminationOperator(ExternalSortOperator operator) {
		sortOp= operator;
		Tuple tmp= operator.getNextTuple();
		prevTuple= null;
		currTuple= tmp;
	}
	
	public DuplicateEliminationOperator(SortOperator operator) {
		sortOp= operator;
		Tuple tmp= operator.getNextTuple();
		prevTuple= null;
		currTuple= tmp;
	}

	/** @return the next tuple in the buffer after duplicates are removed */
	@Override
	public Tuple getNextTuple() {
		if (prevTuple == null) {
			prevTuple= currTuple;
			return currTuple;
		}
		while (currTuple != null && currTuple.getTuple().equals(prevTuple.getTuple())) {
			currTuple= sortOp.getNextTuple();
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
		return sortOp.schema();
	}

	@Override
	public void reset() {
		sortOp.reset();
	}

	/** @return the name of the buffer data table */
	@Override
	public String getTableName() {
		return sortOp.getTableName();
	}

}
