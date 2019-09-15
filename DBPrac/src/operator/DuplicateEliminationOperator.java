package operator;

import dataStructure.DataTable;
import dataStructure.Tuple;

public class DuplicateEliminationOperator extends Operator {

	private DataTable sortedBuffer;
	private int ptr;

	public DuplicateEliminationOperator(SortOperator operator) {
		// TODO Auto-generated constructor stub
		sortedBuffer= operator.dump();
	}

	@Override
	public Tuple getNextTuple() {
		ptr+= 1;
		if (ptr < sortedBuffer.cardinality()) {
			if (ptr > 0) {
				while (sortedBuffer.getData(ptr) == sortedBuffer.getData(ptr - 1)) {
					ptr+= 1;
				}
			}
			return new Tuple(sortedBuffer.getData(ptr));
		}
		return null;
	}

	@Override
	public void reset() {
		ptr= -1;
	}

	@Override
	public DataTable dump() {
		DataTable newTable= new DataTable("");
		ptr= -1;
		while (ptr < sortedBuffer.cardinality()) {
			if (ptr > 0) {
				while (sortedBuffer.getData(ptr) == sortedBuffer.getData(ptr - 1)) {
					ptr+= 1;
				}
			}
			newTable.addData(sortedBuffer.getData(ptr));
			ptr+= 1;
		}
		return newTable;
	}

}
