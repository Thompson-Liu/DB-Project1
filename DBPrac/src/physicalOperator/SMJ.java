package physicalOperator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import dataStructure.Tuple;
import fileIO.TupleWriter;
import net.sf.jsqlparser.expression.Expression;
import parser.EvaluateJoin;
import utils.PhysicalPlanWriter;

/** the class for implementing the Sort Merge Join algorithm */
public class SMJ extends Operator {
	private Operator leftOp;
	private Operator rightOp;
	private ArrayList<String> leftColList;
	private ArrayList<String> rightColList;
	private Tuple tr;
	private Tuple ts;
	private Tuple gs;
	private int ptr;     // store the number of lines of gs
	private int count;   // store the number of lines of ts
	private ArrayList<String> schema;
	private boolean useExternal;
	private Expression expr;
	private List<String> joinOrder;

	private Operator leftSortOp;
	private Operator rightSortOp;
	private ArrayList<String> leftSchema;
	private ArrayList<String> rightSchema;
	private int smjBufferSize;
	private String smjTempDir;
	private int tupleIndex;

	/** @return true if the first k pairs of attributes in leftColList and rightColList are equal to
	 * each other for the two given tuples, and false otherwise. */
	private int ensureEqual(Tuple leftTup, Tuple rightTup) {

		int result= 0;
		int count= 0;
		while (count < leftColList.size() && result == 0) {
			result= leftTup.getData(leftSchema.indexOf(leftColList.get(count))) - rightTup
					.getData(rightSchema.indexOf(rightColList.get(count)));
			count++ ;
		}
		return result;
	}

	/** @param tableOrder 
	 * @param bufferSize: the buffer size used in external sort
	 * @param left: the left child operator
	 * @param right: the right child operator
	 * @param joinExpr: the join evaluation to be evaluated
	 * @param alias: the mapping between table names and aliases
	 * @param dir: the prefix of the directory that stores the temporary files generated during external
	 * sort. */
	public SMJ(int bufferSize, Operator left, Operator right, Expression joinExpr, List<String> tableOrder, String dir, boolean useExternal) {
		EvaluateJoin evalJoin= new EvaluateJoin(joinExpr, left.getTableName(), right.getTableName());
		leftColList= evalJoin.getJoinAttributesLeft();
		rightColList= evalJoin.getJoinAttributesRight();
		leftOp= left;
		rightOp= right;
		expr= joinExpr;
		this.useExternal= useExternal;
		smjBufferSize = bufferSize;
		smjTempDir = dir;
		
		if (useExternal) {
			leftSortOp= new ExternalSortOperator(leftOp, leftColList, bufferSize, smjTempDir, leftOp.getTableName());
			rightSortOp= new ExternalSortOperator(rightOp, rightColList, bufferSize, smjTempDir, "right");
		} else {
			leftSortOp= new SortOperator(leftOp, leftColList);
			rightSortOp= new SortOperator(rightOp, rightColList);
		}
		joinOrder = new ArrayList<String>(tableOrder);

		tr= leftSortOp.getNextTuple();
		ts= rightSortOp.getNextTuple();
		gs= null;
		ptr= 0;
		count= 0;
		leftSchema= leftSortOp.schema();
		rightSchema= rightSortOp.schema();

		// construct the new schema, enforcing join order
		schema = new ArrayList<String>();		
		int tableIndex = joinOrder.indexOf(rightSortOp.getTableName());
		int counter = 0;
		tupleIndex = leftSchema.size();
		String strCounter = "";
		
		if (tableIndex == tableOrder.size() - 1) {
			schema.addAll(leftSchema);
			schema.addAll(rightSchema);
		} else {
			for (int i = 0; i < leftSchema.size(); ++i) {
				String curName = leftSchema.get(i).split("\\.")[0];
				if (!curName.equals(strCounter)) {
					if (counter == tableIndex) {
						tupleIndex = (i--);
						schema.addAll(rightSchema);
						counter++;
						continue;
					}
					counter++;
					strCounter = curName;
				}
				schema.add(leftSchema.get(i));
			}
		}
	}

	@Override
	public Tuple getNextTuple() {
		while (tr != null && ts != null) {
			if (gs == null) {
				while (ensureEqual(tr, ts) < 0) {
					tr= leftSortOp.getNextTuple();
					if (tr == null) {
						if (useExternal) {
							((ExternalSortOperator) leftSortOp).deleteFile();
							((ExternalSortOperator) rightSortOp).deleteFile();
						}
						return null;
					}
				}

				while (ensureEqual(tr, ts) > 0) {
					ts= rightSortOp.getNextTuple();
					if (ts == null) {
						if (useExternal) {
							((ExternalSortOperator) leftSortOp).deleteFile();
							((ExternalSortOperator) rightSortOp).deleteFile();
						}
						return null;
					}
					count++ ;
				}
				gs= new Tuple(ts.getTuple());
				ptr= count;
			}

			Tuple joinedTuple= null;
			if (ensureEqual(tr, ts) == 0) {
				joinedTuple= new Tuple();

				// Generate the combined tuple result
				if (tupleIndex >= leftSchema.size()) {
					ArrayList<Integer> leftTuple = new ArrayList<Integer>(tr.getTuple());
					leftTuple.addAll(ts.getTuple());
					joinedTuple = new Tuple(leftTuple);
				} else {
					for (int i = 0; i < leftOp.schema().size(); i++ ) {
						if (i == tupleIndex) {
							for (int j = 0; j < rightOp.schema().size(); ++j) {
								joinedTuple.addData(ts.getData(j));
							}
						}
						joinedTuple.addData(tr.getData(i));
					}
				}

				ts= rightSortOp.getNextTuple();
				count++ ;
				if (ts == null || ensureEqual(tr, ts) != 0) {
					tr= leftSortOp.getNextTuple();
					gs= null;

					if (useExternal) {
						((ExternalSortOperator) rightSortOp).resetIndex(ptr);
					} else {
						((SortOperator) rightSortOp).resetIndex(ptr);
					}
					ts= rightSortOp.getNextTuple();
					count= ptr;
				}
//				System.out.println(joinedTuple.getTuple());
				return joinedTuple;
			}

			if (useExternal) {
				((ExternalSortOperator) rightSortOp).resetIndex(ptr);
			} else {
				((SortOperator) rightSortOp).resetIndex(ptr);
			}
			ts= rightSortOp.getNextTuple();
			count= ptr;

			tr= leftSortOp.getNextTuple();
			gs= null;
		}
		if (useExternal) {
			((ExternalSortOperator) leftSortOp).deleteFile();
			((ExternalSortOperator) rightSortOp).deleteFile();
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
		leftOp.reset();
		rightOp.reset();
		
		if (useExternal) {
			leftSortOp= new ExternalSortOperator(leftOp, leftColList, smjBufferSize, smjTempDir, leftOp.getTableName());
			rightSortOp= new ExternalSortOperator(rightOp, rightColList, smjBufferSize, smjTempDir, "right");
		} else {
			leftSortOp= new SortOperator(leftOp, leftColList);
			rightSortOp= new SortOperator(rightOp, rightColList);
		}

		tr= leftSortOp.getNextTuple();
		Tuple firstTuple= rightSortOp.getNextTuple();
		ts= firstTuple;
		gs= firstTuple;
	}

	@Override
	public ArrayList<String> schema() {
		return this.schema;
	}

	@Override
	public String getTableName() {
		String tableName = "";
		for (int i = 0; i < joinOrder.size() - 1; ++i) {
			tableName += (joinOrder.get(i) + ",");
		}
		tableName += (joinOrder.get(joinOrder.size() - 1));
		return tableName;
	}

	public Operator getLeftChild() {
		return leftSortOp;
	}

	public Operator getRightChild() {
		return rightSortOp;
	}

	public Expression getJoinExpression() {
		return expr;
	}

	@Override
	public void accept(PhysicalPlanWriter ppw) {
		try {
			ppw.visit(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}