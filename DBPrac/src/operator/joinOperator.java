package operator;

import java.util.*;

import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import parser.EvaluateExpression;

public class joinOperator extends Operator {
	
	private String tableName;
	private DataTable joinResultTable;
	private ArrayList<Table> joinTables;
	
	
	public DataTable getJoinTables(PlainSelect plainSelect) {
		Table left = (Table) plainSelect.getFromItem();
		for (Iterator joinsIt = plainSelect.getJoins().iterator(); joinsIt.hasNext();) {
			Join join = (Join) joinsIt.next();
			//to produced after WHERE result
			SelectOperator right = new SelectOperator(join.toString(),plainSelect.getWhere());
			joinResultTable = twoTableJoin(joinResultTable, right.dump(),plainSelect.getWhere());
		}
		return joinResultTable;
	}
	
	// Take in the WHEREpreprocesed left table and fully join with the right table
	private DataTable twoTableJoin(DataTable left, DataTable right, Expression where) {
		ArrayList<String> leftSchema = left.getSchema();
		ArrayList<String> rightSchema = right.getSchema();
		leftSchema.addAll(rightSchema);
		DataTable temp = new DataTable(tableName,leftSchema);
//		for(int i=0; i<left.cardinality();i++) {
//			for(int j=0; j<right.cardinality(); j++) {
//				EvaluateExpression whereVisitor = new EvaluateWhere(next, tableName);
//				if ((next = exprVisitor.evaluate(exp)) != null) {
//					return temp.addData(next);
//				} 
//			}
//		}
		return temp;
	}
	
	//store the result in a txt
	public void dump() {
		
	}
	
	
	
	

}
