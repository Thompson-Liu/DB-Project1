package physicalOperator;

import java.util.ArrayList;

import dataStructure.Tuple;
import fileIO.BinaryTupleWriter;
import fileIO.TupleWriter;

public class SMJ extends Operator {
	private Tuple tr;
	private Tuple ts;
	private Tuple gs;

	private boolean ensureEqual(Tuple leftTup, Tuple rightTup, ArrayList<String> leftColList,
		ArrayList<String> rightColList, ArrayList<String> leftSchema, ArrayList<String> rightSchema) {
		for (int i= 0; i < leftColList.size(); i+= 1) {
			if (leftTup.getData(leftSchema.indexOf(leftColList.get(i))) != rightTup
				.getData(rightSchema.indexOf(rightColList.get(i)))) { return false; }
		}
		return true;
	}

	public SMJ(int bufferSize, ArrayList<String> leftColList, ArrayList<String> rightColList, Operator leftOp,
		Operator rightOp) {
//		ArrayList<String> newSchema= new ArrayList<String>();
//		for (String col : leftSchema) {
//			if (!leftColList.contains(col)) {
//				newSchema.add(col);
//			}
//		}
//		for (String col : rightSchema) {
//			if (!rightColList.contains(col)) {
//				newSchema.add(col);
//			}
//		}
//		data= new DataTable("SMJJoin", newSchema);
		ExternalSortOperator leftExSortOp= new ExternalSortOperator(leftOp, leftColList, bufferSize, "/tempdir/");
		ExternalSortOperator rightExSortOp= new ExternalSortOperator(rightOp, rightColList, bufferSize,
			"/tempdir/");
		tr= leftExSortOp.getNextTuple();
		Tuple firstTuple= rightExSortOp.getNextTuple();
		ts= firstTuple;
		gs= firstTuple;
		while (tr != null && gs != null) {
			for (int i= 0; i < leftColList.size(); i+= 1) {
				while (tr.getData(leftOp.schema().indexOf(leftColList.get(i))) < gs
					.getData(rightOp.schema().indexOf(rightColList.get(i)))) {
					tr= leftExSortOp.getNextTuple();
				}
				while (tr.getData(leftOp.schema().indexOf(leftColList.get(i))) > gs
					.getData(rightOp.schema().indexOf(rightColList.get(i)))) {
					gs= rightExSortOp.getNextTuple();
				}
			}
			ts= gs;
			BinaryTupleWriter tupleWrite= new BinaryTupleWriter(
				"/tempDir/" + "SMJ");
			while (ensureEqual(tr, gs, leftColList, rightColList, leftOp.schema(), rightOp.schema())) {
				ts= gs;
				while (ensureEqual(tr, ts, leftColList, rightColList, leftOp.schema(), rightOp.schema())) {
					Tuple joinedTuple= tr;
//					for (int i= 0; i < leftSchema.size(); i++ ) {
//						if (!leftColList.contains(leftSchema.get(i))) {
//							joinedEntry.addData(tr.getData(i));
//						}
//					}
					for (int i= 0; i < rightOp.schema().size(); i++ ) {
						joinedTuple.addData(ts.getData(i));
					}
					tupleWrite.addNextTuple(joinedTuple);
					ts= rightExSortOp.getNextTuple();
				}
				gs= ts;
			}
		}
	}

	@Override
	public Tuple getNextTuple() {
		return null;
	}

	@Override
	public void dump(TupleWriter writer) {
		writer.write(getData().toArrayList());
		writer.close();
	}

	@Override
	public DataTable getData() {
		DataTable temp= new DataTable(dataFile, schema);
		return temp;
	}

}
