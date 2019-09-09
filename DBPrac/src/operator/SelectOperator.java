package operator;

import java.io.BufferedReader;
import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;
import parser.EvaluateExpression;

public class SelectOperator extends ScanOperator {

	private String tableName;
	PlainSelect select;
	
	public SelectOperator (String name, PlainSelect selectClause) {
		super(name);
		tableName = name;
		select = selectClause;
	}
	
	public Tuple getNextTuple(){
		Tuple next;
		while ((next = super.getNextTuple()) != null) {
			EvaluateExpression exprVisitor = new EvaluateExpression(next, tableName);
			if ((next = exprVisitor.evaluate(select)) != null) {
				return next;
			} else {
				super.removeLastTuple();
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
