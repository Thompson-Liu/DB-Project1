package physicalOperator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import dataStructure.Buffer;
import dataStructure.Tuple;
import fileIO.TupleWriter;
import net.sf.jsqlparser.expression.Expression;
import parser.EvaluateWhere;
import utils.PhysicalPlanWriter;

/** The BNLJ physcial operator that will join tuples according to the BNLJ algorithm
 * 
 * @author mingzhaoliu */
public class BNLJ extends Operator {

	private Buffer buffer;
	private int numOuters;
	private Operator outerOp;
	private Operator innerOp;
	private Expression joinCond;
	private boolean bufState= true;
	private boolean tupState= true;
	private int bufTupState= 0;
	private String tableName;
	private ArrayList<String> schema;
	private Tuple innerTup;
	private EvaluateWhere eval;

	/** The constructor of BNLJ physcial operator
	 * 
	 * @param numPages number of pages allowed for the buffer
	 * @param outer outer operator
	 * @param inner inner operator
	 * @param joinExp join expression 
	 * @param tableOrder2 */
	public BNLJ(int numPages, Operator outer, Operator inner, Expression joinExp, List<String> tableOrder) {
		numOuters= (int) Math.floor(1.0 * numPages * 4096 / 4 / (outer.schema().size()));
		buffer= new Buffer(numOuters);

		outerOp= outer;
		innerOp= inner;
		joinCond= joinExp;

		// construct the new schema
		List<String> outerSchema = outer.schema();
		schema = new ArrayList<String>();		
		int tableIndex = tableOrder.indexOf(inner.getTableName());
		int tupleIndex = outer.schema().size();
		int counter = 0;
		String strCounter = "";

		if (tableIndex == tableOrder.size() - 1) {
			schema.addAll(outerSchema);
			schema.addAll(inner.schema());
		} else {
			for (int i = 0; i < outerSchema.size(); ++i) {
				String curName = outerSchema.get(i).split("\\.")[0];
				if (!curName.equals(strCounter)) {
					if (counter == tableIndex) {
						tupleIndex = (i--);
						schema.addAll(inner.schema());
						counter++;
						continue;
					}
					counter++;
					strCounter = curName;
				}
				schema.add(outerSchema.get(i));
			}
		}


		// Construct the new table name, enforcing join order
		for (int i = 0; i < tableOrder.size() - 1; ++i) {
			tableName += (tableOrder.get(i) + ",");
		}
		tableName += (tableOrder.get(tableOrder.size() - 1));
		eval= new EvaluateWhere(joinCond, outer.schema(), inner.schema(), tupleIndex);
	}

	/** Populate the buffer */
	private void populateBuffer() {
		buffer.clear();
		Tuple tup;
		while (!buffer.overflow() && (tup= outerOp.getNextTuple()) != null) {
			buffer.addData(tup);
		}
	}

	@Override
	public Tuple getNextTuple() {
		Tuple next= null;
		Tuple outerTup;
		boolean flag= true;

		while (flag) {
			// if need to get another block, repopulate the buffer
			if (bufState) {
				populateBuffer();

				// if the new buffer is empty, indicating that nothing left from outer,
				// then return null
				if (buffer.empty()) { return null; }

				// if need to get another tuple from inner S
				if (tupState) {
					while ((innerTup= innerOp.getNextTuple()) != null) {
						while ((outerTup= buffer.getTuple(bufTupState++ )) != null) {
							if ((next= eval.evaluate(outerTup, innerTup)) != null) {
								tupState= false;
								bufState= false;
								System.out.println(next.getTuple().toString());
								return next;
							}
						}
						bufTupState= 0;
					}
					innerOp.reset();
				} else {
					while ((outerTup= buffer.getTuple(bufTupState++ )) != null) {
						if ((next= eval.evaluate(outerTup, innerTup)) != null) {
							bufState= false;
							System.out.println(next.getTuple().toString());
							return next;
						}
					}
					bufTupState= 0;
					bufState= false;
					tupState= true;
				}
			}

			else {
				if (tupState) {
					while ((innerTup= innerOp.getNextTuple()) != null) {
						while ((outerTup= buffer.getTuple(bufTupState++ )) != null) {
							if ((next= eval.evaluate(outerTup, innerTup)) != null) {
								tupState= false;
								System.out.println(next.getTuple().toString());
								return next;
							}
						}
						bufTupState= 0;
					}
					innerOp.reset();
					bufState= true;
				} else {
					while ((outerTup= buffer.getTuple(bufTupState++ )) != null) {
						if ((next= eval.evaluate(outerTup, innerTup)) != null) { 
							System.out.println(next.getTuple().toString());
							return next; 
						}
					}
					bufTupState= 0;
					tupState= true;
				}
			}
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
		reset();
		writer.close();
	}

	@Override
	public void reset() {
		bufState= true;
		tupState= true;
		bufTupState= 0;
		innerOp.reset();
		outerOp.reset();
	}

	@Override
	public ArrayList<String> schema() {
		return schema;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	public Operator getOuterOperator() {
		return outerOp;
	}

	public Operator getInnerOperator() {
		return innerOp;
	}

	public Expression getExpression() {
		return joinCond;
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
