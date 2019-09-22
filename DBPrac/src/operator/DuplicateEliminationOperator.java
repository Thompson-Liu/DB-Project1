package operator;

import java.io.PrintStream;
import java.util.Arrays;

import dataStructure.DataTable;
import dataStructure.Tuple;

public class DuplicateEliminationOperator extends Operator {

	private DataTable sortedBuffer;
	int ptr;

	public DuplicateEliminationOperator(SortOperator operator) {
		// TODO Auto-generated constructor stub
		DataTable tmpTable= operator.getData();
		sortedBuffer= new DataTable("", operator.schema());
		int i= 0;
		while (i < tmpTable.cardinality()) {
			if (i > 0) {
				while (Arrays.equals(tmpTable.getRow(i), tmpTable.getRow(i - 1))) {
					i+= 1;
				}
			}
			sortedBuffer.addData(tmpTable.getRow(i));
			i+= 1;
		}
	}

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

//	@Override
//	public void getData() {
//		return
//		
//	}
}
