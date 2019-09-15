package operator;

import java.io.BufferedReader;
import java.util.ArrayList;

import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;
import parser.EvaluateWhere;

public class SelectOperator extends Operator {

	private Expression exp;
	private Operator childOp;
	
	public SelectOperator (Expression expression, Operator op) {
		exp = expression;
		childOp = op;
	}
	
	public Tuple getNextTuple(String tableName){
		Tuple next;
		EvaluateWhere exprVisitor = new EvaluateWhere(exp);
		while ((next = childOp.getNextTuple(tableName)) != null) {
			if ((next = exprVisitor.evaluate(null, next, new ArrayList<String>(), table.getSchema())) != null) {
				return next;
			} 
		}
		return null;
	}
	
	public void reset() {
		childOp.reset();
	}
	
	public DataTable dump(String tableName) {
		DataTable data = new DataTable("Output", new ArrayList<String>());
		Tuple tup = new Tuple();
		while ((tup = getNextTuple(tableName)) != null) {
			data.addData(tup.getTuple());
		}
		return data;
	}
}
