package operator;

import java.io.PrintStream;
import java.util.List;

import dataStructure.DataTable;
import dataStructure.Tuple;

public class SortOperator extends Operator {

	private DataTable buffer;
	private int ptr;

	public SortOperator(Operator childOp, List<String> colList) {
		ptr= -1;
		buffer= childOp.getData();
//		System.out.println(buffer.cardinality());
		buffer.printTableInfo();
//		System.out.println(childOp.schema());
		if (colList == null) {
			buffer.sortData(buffer.getSchema());
		} else {
			buffer.sortData(colList);
		}
	}

	@Override
	public Tuple getNextTuple() {
		ptr+= 1;
		if (ptr < buffer.cardinality()) return new Tuple(buffer.getRow(ptr));
		return null;

	}

	@Override
	public void reset() {
		ptr= -1;
	}

	@Override
	public void dump(PrintStream ps, boolean print) {
		buffer.printTable(ps);
	}

	@Override
	public DataTable getData() {
		// dump not needed because buffer is initialized upon construction
		return buffer;
	}

}
