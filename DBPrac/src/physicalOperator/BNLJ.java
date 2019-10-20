package physicalOperator;

import java.util.ArrayList;
import java.util.HashMap;

import dataStructure.Buffer;
import dataStructure.DataTable;
import dataStructure.Tuple;
import fileIO.TupleWriter;
import net.sf.jsqlparser.expression.Expression;
import parser.EvaluateWhere;

public class BNLJ extends Operator {

	private static Buffer buffer;
	private int numOuters;
	private Operator outerOp;
	private Operator innerOp;
	private Expression joinCond;
	private DataTable data;
	private HashMap<String, String> alias;
	private boolean bufState = true;
	private boolean tupState = true;
	private int bufTupState = 0;
	
	private Tuple innerTup;
	private EvaluateWhere eval;


	public BNLJ(int numPages, Operator outer, Operator inner, 
			Expression joinExp, HashMap<String, String> tableAlias) {
		
		numOuters = (int) Math.floor(1.0 * numPages * 4096 / 4 / (outer.schema().size()));
		buffer = new Buffer(numOuters);

		outerOp = outer;
		innerOp = inner;
		joinCond = joinExp;
		ArrayList<String> cur= new ArrayList<String>(outer.schema());
		cur.addAll(inner.schema());
		data = new DataTable(outer.getTableName() + " " + inner.getTableName(), cur);
		alias = tableAlias;
		
		eval = new EvaluateWhere(joinCond, innerOp.schema(), outerOp.schema(), alias);
	}

	private void populateBuffer() {
		buffer.clear();
		Tuple tup;
		while(!buffer.overflow() && (tup = outerOp.getNextTuple()) != null) {
			buffer.addData(tup);
		}
	}

	@Override
	public Tuple getNextTuple() {
		Tuple next = null;
		Tuple outerTup;
		boolean flag = true;

		while (flag) {
			// if need to get another block, repopulate the buffer
			if (bufState)  {
				populateBuffer();
				
				// if the new buffer is empty, indicating that nothing left from outer, 
				// then return null
				if(buffer.empty()) {
					return null;
				}

				// if need to get another tuple from inner S
				if (tupState) {
					while ((innerTup = innerOp.getNextTuple()) != null) {
						while ((outerTup = buffer.getTuple(bufTupState++)) != null) {
							if ((next = eval.evaluate(outerTup, innerTup)) != null) {
//								data.addData(next);
								tupState = false;
								bufState = false;
								return next;
							}
						} 
						bufTupState = 0;
					}
					innerOp.reset();
				} else {
					while ((outerTup = buffer.getTuple(bufTupState++)) != null) {
						if ((next = eval.evaluate(outerTup, innerTup)) != null) {
//							data.addData(next);
							bufState = false;
							return next;
						}
					}
					bufTupState = 0;
					bufState = false;
					tupState = true;
				}
			} 
			
			else {
				if (tupState) {
					while ((innerTup = innerOp.getNextTuple()) != null) {
						while ((outerTup = buffer.getTuple(bufTupState++)) != null) {
							if ((next = eval.evaluate(outerTup, innerTup)) != null) {
//								data.addData(next);
								tupState = false;
								return next;
							}
						} 
						bufTupState = 0;
					}
					innerOp.reset();
					bufState = true;
				} else {
					while ((outerTup = buffer.getTuple(bufTupState++)) != null) {
						if ((next = eval.evaluate(outerTup, innerTup)) != null) {
//							data.addData(next);
							return next;
						}
					}
					bufTupState = 0;
					tupState = true;
				}
			}
		}
		return null;
	} 
	
	@Override 
	public void dump(TupleWriter writer) {
		Tuple t; 
		while ((t = getNextTuple()) != null) {
			writer.addNextTuple(t);
		}
		writer.dump();
		writer.close();
	}

	@Override
	public void reset() {
		bufState = true;
		tupState = true;
		bufTupState = 0;
		innerOp.reset();
		outerOp.reset();
	}

	@Override
	public ArrayList<String> schema() {
		return data.getSchema();
	}

	@Override
	public String getTableName() {
		return data.getTableName();
	}

	@Override
	public DataTable getData() {
		Tuple t;
		while ((t = getNextTuple()) != null) {

		}
		reset();
		return data;
	}
}
