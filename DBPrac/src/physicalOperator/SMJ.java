package physicalOperator;

import java.util.ArrayList;
import java.util.HashMap;

import dataStructure.Tuple;
import fileIO.TupleWriter;
import net.sf.jsqlparser.expression.Expression;
import parser.EvaluateJoin;

/** the class for implementing the Sort Merge Join algorithm */
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
	private int ptr;
	private boolean flag;
	private boolean stop;
	private ArrayList<String> schema;

	/** @return true if the first k pairs of attributes in leftColList and rightColList are equal to
	 * each other for the two given tuples, and false otherwise. */
	private boolean ensureEqual(Tuple leftTup, Tuple rightTup, ArrayList<String> leftColList,
		ArrayList<String> rightColList, ArrayList<String> leftSchema, ArrayList<String> rightSchema, int k) {
		for (int i= 0; i < k; i+= 1) {
			if (leftTup.getData(leftSchema.indexOf(leftColList.get(i))) != rightTup
				.getData(rightSchema.indexOf(rightColList.get(i)))) { return false; }
		}
		return true;
	}

	/** @param bufferSize: the buffer size used in external sort
	 * @param left: the left child operator
	 * @param right: the right child operator
	 * @param joinExpr: the join evaluation to be evaluated
	 * @param alias: the mapping between table names and aliases
	 * @param dir: the prefix of the directory that stores the temporary files generated during external
	 * sort. */
	public SMJ(int bufferSize, Operator left, Operator right, Expression joinExpr, HashMap<String, String> alias,
		String dir) {
		EvaluateJoin evalJoin= new EvaluateJoin(joinExpr, left.getTableName(), right.getTableName(), alias);
		leftColList= evalJoin.getJoinAttributesLeft();
		rightColList= evalJoin.getJoinAttributesRight();
		leftOp= left;
		rightOp= right;
		leftExSortOp= new ExternalSortOperator(leftOp, leftColList, bufferSize, dir, "left");
		rightExSortOp= new ExternalSortOperator(rightOp, rightColList, bufferSize, dir, "right");
		tr= leftExSortOp.getNextTuple();
		Tuple firstTuple= rightExSortOp.getNextTuple();
		ts= firstTuple;
		gs= firstTuple;
		ptr= 0;
		this.schema= new ArrayList<String>(left.schema());
		this.schema.addAll(right.schema());
		flag= false;
		stop= false;
	}

	@Override
	public Tuple getNextTuple() {
		while (tr != null && gs != null) {

//			if (tr.getData(0) == 134 && tr.getData(1) == 22) {
//				System.out.println("heiiiii");
//			}
			if (!flag) {
				int i= 0;
				while (i < leftColList.size()) {
					while (tr.getData(leftOp.schema().indexOf(leftColList.get(i))) < gs
						.getData(rightOp.schema().indexOf(rightColList.get(i)))) {
//						System.out.println(tr.printData());
						tr= leftExSortOp.getNextTuple();
						if (tr == null) return null;
						if (!ensureEqual(tr, gs, leftColList, rightColList, leftOp.schema(), rightOp.schema(), i)) {
							i= 0;
							break;
						}
					}
					while (tr.getData(leftOp.schema().indexOf(leftColList.get(i))) > gs
						.getData(rightOp.schema().indexOf(rightColList.get(i)))) {
//						rightExSortOp.resetIndex(ptr);
						rightExSortOp.resetIndex(ptr);
						gs= rightExSortOp.getNextTuple();
						if (gs == null) return null;
						ptr+= 1;
						if (!ensureEqual(tr, gs, leftColList, rightColList, leftOp.schema(), rightOp.schema(), i)) {
							i= -1;
							break;
						}
					}
					i+= 1;
				}
				rightExSortOp.resetIndex(ptr);
				ts= new Tuple(gs.getTuple());
			}
			if (tr == null || gs == null) return null;
			if (ensureEqual(tr, gs, leftColList, rightColList, leftOp.schema(), rightOp.schema(),
				leftColList.size())) {

				if (ts != null && ensureEqual(tr, ts, leftColList, rightColList, leftOp.schema(), rightOp.schema(),
					leftColList.size())) {

					flag= true;
					Tuple joinedTuple= new Tuple();
					for (int j= 0; j < leftOp.schema().size(); j++ ) {
						joinedTuple.addData(tr.getData(j));
					}
					for (int j= 0; j < rightOp.schema().size(); j++ ) {
						joinedTuple.addData(ts.getData(j));
					}
					ts= rightExSortOp.getNextTuple();
//					if(ts.getData(0)==131 && ts.getData(1)==35) {
//						System.out.println(ts.printData());
//					}

					return joinedTuple;
				}
			}
			flag= false;
			tr= leftExSortOp.getNextTuple();
			rightExSortOp.resetIndex(ptr);
		}
		return null;
	}

	@Override
	public void dump(TupleWriter writer) {
		Tuple t;
		while ((t= getNextTuple()) != null) {
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
		return this.schema;
	}

	@Override
	public String getTableName() {
		return leftOp.getTableName() + "," + rightOp.getTableName();
	}
}
