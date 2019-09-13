package operator;

import java.io.BufferedReader;
import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;
import parser.EvaluateExpression;

public class SelectOperator extends ScanOperator{

	private String tableName;
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
	private DataTable data;
	private Expression exp;
	
	public SelectOperator (String tableName, Expression expression) {
		super(tableName);
		exp = expression;
=======
	private PlainSelect plainSelect;
	
=======
	private PlainSelect plainSelect;
	
>>>>>>> parent of 7f41304... expressionvisitor
=======
	private PlainSelect plainSelect;
	
>>>>>>> parent of 7f41304... expressionvisitor
	public SelectOperator (String name, PlainSelect ps) {
		super(name);
		tableName = name;
		plainSelect = ps;
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> parent of 7f41304... expressionvisitor
=======
>>>>>>> parent of 7f41304... expressionvisitor
=======
>>>>>>> parent of 7f41304... expressionvisitor
	}
	
	public Tuple getNextTuple(){
		Tuple next;
		while ((next = this.getNextTuple()) != null){
			EvaluateExpression exprVisitor = new EvaluateExpression(next, tableName);
			if ((next = exprVisitor.evaluate(plainSelect)) != null) {
				return next;
			} else {
				this.removeLastTuple();
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
