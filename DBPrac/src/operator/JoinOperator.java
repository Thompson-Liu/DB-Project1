/**
 * Takes in a joined left table and right table to scan
 */
package operator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
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
	private boolean resetFlag = true;
	private Tuple left;


	public JoinOperator(Operator LeftOperator, Operator RightOperator, Expression expression) {
		joinExp=expression;
		ArrayList<String> cur = (ArrayList<String>) LeftOperator.schema().clone();
		cur.addAll(RightOperator.schema());
		currentTable = new DataTable(LeftOperator.getTableName()+" "+RightOperator.getTableName(), cur);
		leftOperator = LeftOperator;
		rightOperator = RightOperator;
	}

	public void reset() {
		leftOperator.reset();
		rightOperator.reset();
	}

	public String getTableName() {
		return currentTable.getTableName();
	}

	// return the schema of the current table
	public ArrayList<String> Schema(){
		return currentTable.getSchema();
	}

	//  could use scan or could also use right table directly as input
	public Tuple getNextTuple() {
		Tuple next = null;
		boolean flag = true;
		Tuple right;
		EvaluateWhere evawhere = new EvaluateWhere(joinExp,leftOperator.schema(), 
				rightOperator.schema(),leftOperator.getTableName(),rightOperator.getTableName());
		
		while (flag) {
			if(resetFlag) {
				while ((left = leftOperator.getNextTuple()) != null) {
					while((right = rightOperator.getNextTuple()) != null) {
						if((next = evawhere.evaluate(left,right)) != null) {
							currentTable.addData(next);
							resetFlag = false;
							return next;
						}
					}
				}
				flag = false;
			} else {
				while((right=rightOperator.getNextTuple())!=null) {
					if((next=evawhere.evaluate(left,right))!=null) {
						currentTable.addData(next);
						return next;
					}
				}
				rightOperator.reset();
				resetFlag = true;
			}
		}
		return null;

		//		while(!resetFlag && (left=leftOperator.getNextTuple())!=null) {
		//			while((right=rightOperator.getNextTuple())!=null) {
		//				if((next=evawhere.evaluate(left,right,leftOperator.schema(), rightOperator.schema()))!=null) {
		//					currentTable.addData(next);
		//					return next;
		//				}
		//			}
		//			rightOperator.reset();
		//			resetFlag = true;
		//		}
	}
	

	public void dump(PrintStream ps) {
		Tuple next;
		while((next=getNextTuple())!=null) {

		}
		currentTable.printTable(ps);;
	}

}
