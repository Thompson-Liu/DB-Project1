/**
 * Takes in a joined left table and right table to scan
 */
package operator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;

import dataStructure.Catalog;
import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import parser.EvaluateWhere;

public class JoinOperator extends Operator {

	private DataTable currentTable;
	private Operator leftOperator;
	private Operator rightOperator;
	private Expression joinExp;

	public JoinOperator(Operator LeftOperator, Operator RightOperator, Expression expression) {
		this.joinExp=expression;
		this.leftOperator = LeftOperator;
		this.rightOperator = RightOperator;
	}
	
	// return the schema of the current table
	public ArrayList<String> getSchema(){
		return currentTable.getSchema();
	}
	
	//  could use scan or could also use right table directly as input
	public Tuple getNextTuple() {
		Tuple next;
		Tuple left;
		Tuple right;
		EvaluateWhere evawhere = new EvaluateWhere(joinExp );
		while((left=leftOperator.getNextTuple(this.leftTable.getTableName()))!=null) {
			while((right=rightOperator.getNextTuple())!=null) {
				if((next=evawhere.evaluate(left,right,leftOperator.getSchema(), this.rightTable.getSchema()))!=null) {
					currentTable.addData(next);
					return next;
				}
			}
			rightOperator.reset();
		}
		return null;
	}
		
	public DataTable dump() {
		return this.currentTable;
	}

}
