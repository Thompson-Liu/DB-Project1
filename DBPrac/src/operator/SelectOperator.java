package operator;

import java.io.BufferedReader;
import java.util.ArrayList;

import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;
import parser.EvaluateWhere;

public class SelectOperator extends Operator {

	private DataTable table;
	private Expression exp;
	private Operator childOp;
	
	public SelectOperator (DataTable dt, Expression expression, Operator op) {
		table = dt;
		exp = expression;
		childOp = op;
	}
	
	public Tuple getNextTuple(){
		Tuple next;
		EvaluateWhere exprVisitor = new EvaluateWhere(exp);
		while ((next = childOp.getNextTuple()) != null) {
			if ((next = exprVisitor.evaluate(null, next, new ArrayList<String>(), table.getSchema())) != null) {
				return next;
			} 
		}
		return null;
	}
	
	public void reset() {
		super.reset();
	}
	
	public DataTable dump() {
		return super.dump();
	}
}
