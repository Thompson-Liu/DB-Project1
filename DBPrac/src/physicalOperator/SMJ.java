package physicalOperator;

import java.util.ArrayList;
import java.util.HashMap;

import dataStructure.Tuple;
import fileIO.TupleWriter;
import net.sf.jsqlparser.expression.Expression;
import parser.EvaluateJoin;

public class SMJ extends Operator {
	private Operator leftOp;
	private Operator rightOp;
	private ArrayList<String> leftColList;
	private ArrayList<String> rightColList;
	private ExternalSortOperator leftExSortOp;
	private ExternalSortOperator rightExSortOp;
	private Tuple tr;
	private Tuple ts;
	private Tuple gs;
	boolean flag;

	private boolean ensureEqual(Tuple leftTup, Tuple rightTup, ArrayList<String> leftColList,
		ArrayList<String> rightColList, ArrayList<String> leftSchema, ArrayList<String> rightSchema, int k) {
		for (int i= 0; i < k; i+= 1) {
			if (leftTup.getData(leftSchema.indexOf(leftColList.get(i))) != rightTup
				.getData(rightSchema.indexOf(rightColList.get(i)))) { return false; }
		}
		return true;
	}

	public SMJ(int bufferSize, Operator left, Operator right, Expression joinExpr, HashMap<String, String> alias, String dir) {
		EvaluateJoin evalJoin= new EvaluateJoin(joinExpr, left.getTableName(), right.getTableName(), alias);
		leftColList= evalJoin.getJoinAttributesLeft();
		rightColList= evalJoin.getJoinAttributesRight();
		leftOp= left;
		rightOp= right;
		leftExSortOp= new ExternalSortOperator(leftOp, leftColList, bufferSize, dir);
		rightExSortOp= new ExternalSortOperator(rightOp, rightColList, bufferSize, dir);
		tr= leftExSortOp.getNextTuple();
		Tuple firstTuple= rightExSortOp.getNextTuple();
		ts= firstTuple;
		gs= firstTuple;
		flag= false;
	}

	@Override
	public Tuple getNextTuple() {
		while (tr != null && gs != null) {
			if (!flag) {
				int i= 0;
				while (i < leftColList.size()) {
					while (tr != null && gs != null && tr.getData(leftOp.schema().indexOf(leftColList.get(i))) < gs
						.getData(rightOp.schema().indexOf(rightColList.get(i)))) {
						tr= leftExSortOp.getNextTuple();
						if (!ensureEqual(tr, gs, leftColList, rightColList, leftOp.schema(), rightOp.schema(), i)) {
							i= 0;
							break;
						}
					}
					while (tr != null && gs != null && tr.getData(leftOp.schema().indexOf(leftColList.get(i))) > gs
						.getData(rightOp.schema().indexOf(rightColList.get(i)))) {
						gs= rightExSortOp.getNextTuple();
						if (!ensureEqual(tr, gs, leftColList, rightColList, leftOp.schema(), rightOp.schema(), i)) {
							i= -1;
							break;
						}
					}
					i+= 1;
				}
				ts= new Tuple(gs.getTuple());
			}
			if (tr == null || gs == null) {
				return null;
			}
//			while (ensureEqual(tr, gs, leftColList, rightColList, leftOp.schema(), rightOp.schema(),
//				leftColList.size())) {
//				ts= gs;
			if (ts != null && ensureEqual(tr, ts, leftColList, rightColList, leftOp.schema(), rightOp.schema(),
				leftColList.size())) {
				flag= true;
				Tuple joinedTuple= new Tuple();
				System.out.println(tr.printData());
				for (int j= 0; j < leftOp.schema().size(); j++ ) {
					joinedTuple.addData(tr.getData(j));
				}
				for (int j= 0; j < rightOp.schema().size(); j++ ) {
					joinedTuple.addData(ts.getData(j));
				}
				ts= rightExSortOp.getNextTuple();
				return joinedTuple;
			}
			gs= new Tuple(ts.getTuple());
//			}
			flag= false;
		}
		return null;
	}

	@Override
	public void dump(TupleWriter writer) {
		Tuple t;
		int counter=1;
		while ((t= getNextTuple()) != null) {
			System.out.println(counter++);
			System.out.println(t.printData());
			writer.addNextTuple(t);
		}
		writer.dump();
		writer.close();
	}

	@Override
	public void reset() {
		leftExSortOp.reset();
		rightExSortOp.reset();
		tr= leftExSortOp.getNextTuple();
		Tuple firstTuple= rightExSortOp.getNextTuple();
		ts= firstTuple;
		gs= firstTuple;
	}

	@Override
	public ArrayList<String> schema() {
		leftOp.schema().addAll(rightOp.schema());
		return leftOp.schema();
	}

	@Override
	public String getTableName() {
		return leftOp.getTableName() + "," + rightOp.getTableName();
	}
}
