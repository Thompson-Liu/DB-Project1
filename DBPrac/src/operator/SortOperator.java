package operator;

import dataStructure.DataTable;
import dataStructure.Tuple;

public class SortOperator extends Operator {

	private DataTable buffer;
	private int ptr;

	public SortOperator(Operator childOp, String colName) {
		ptr= -1;
		buffer= childOp.dump();
		buffer.sortData(colName);
	}

	@Override
	public Tuple getNextTuple() {
		ptr+= 1;
		if (ptr < buffer.cardinality()) return new Tuple(buffer.getData(ptr));
		return null;

	}

	@Override
	public void reset() {
		ptr= -1;
	}

	@Override
	public DataTable dump() {
		return buffer;
	}

}
