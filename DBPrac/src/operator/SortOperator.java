package operator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import dataStructure.DataTable;
import dataStructure.Tuple;

public class SortOperator extends Operator {

	private DataTable buffer;
	private int ptr;

	public SortOperator(Operator childOp, List<String> colList) {
		ptr= -1;
		buffer= new DataTable(childOp.getTableName(), childOp.schema());
		buffer.setFullTable(childOp.getData().getFullTable());
//		buffer.addData(childOp.getData().);
//		System.out.println(buffer.cardinality());
//		buffer.printTableInfo();
//		System.out.println(childOp.schema());
		if (colList == null) {
			System.out.println(childOp.schema());
			buffer.sortData(childOp.schema(), childOp.schema());
		} else {
			buffer.sortData(colList, childOp.schema());
		}
	}

	@Override
	public Tuple getNextTuple() {
		ptr+= 1;
		if (ptr < buffer.cardinality()) return new Tuple(buffer.getRow(ptr));
		return null;

	}

	@Override
	public ArrayList<String> schema() {
		return buffer.getSchema();
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
//		buffer.printTable(System.out);
		return buffer;
	}

	@Override
	public String getTableName() {
		return buffer.getTableName();
	}

}
