package operator;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.expression.Expression;
import parser.EvaluateWhere;

public class SelectOperator extends Operator {

	private Expression exp;
	private Operator childOp;
	private DataTable data;
	private HashMap<String,String> tableAlias;

	public SelectOperator(Expression expression, Operator op,HashMap<String,String> tableAlias) {
		exp= expression;
		childOp= op;
		data= new DataTable(op.getTableName(), op.schema());
		this.tableAlias=tableAlias;
	}

	@Override
	public Tuple getNextTuple() {
		Tuple next;
		EvaluateWhere exprVisitor= new EvaluateWhere(exp, new ArrayList<String>(),
			childOp.schema(),tableAlias);
		while ((next= childOp.getNextTuple()) != null) {
			if ((next= exprVisitor.evaluate(null, next)) != null) {
				data.addData(next);
				return next;
			}
		}
		return null;
	}

	@Override
	public void reset() {
		childOp.reset();
	}

	@Override
	public void dump(PrintStream ps, boolean print) {
		Tuple tup;
		while ((tup= getNextTuple()) != null) {

		}
		if (print) { data.printTable(ps); }
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
		dump(System.out, false);
		return data;
	}
}
