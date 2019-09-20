package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dataStructure.DataTable;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import operator.JoinOperator;
import operator.Operator;
import operator.ProjectOperator;
import operator.ScanOperator;
import operator.SelectOperator;

public class OperatorFactory {

	public Operator generateQueryPlan(PlainSelect select) {

		String name= ((Table) (select.getFromItem())).getName();
		boolean whereEmpty;

		// check where clause
		Expression whereClause= select.getWhere();
		if (whereClause == null) {
			whereEmpty= true;
		} else {
			whereEmpty= false;
		}

		
		// check select clause
		List<SelectItem> selectItems= select.getSelectItems();
		if (selectItems.get(0) instanceof AllColumns) {
			if (whereEmpty) {
				return new ScanOperator();
			} else {
				return new SelectOperator(whereClause, new ScanOperator());
			}
		} else {
			if (whereEmpty) {
				return new ProjectOperator(new ScanOperator(), selectItems);
			} else {
				return new ProjectOperator(new SelectOperator(whereClause, new ScanOperator()), selectItems);
			}
		}
		
		SelectOperator selectOp = new SelectOperator(whereClause, new ScanOperator());
		if(select.getJoins()!=null) {
			ProjectOperator left = new ProjectOperator(selectOp, selectItems);
			int joinCount =1;
			for(Iterator joinsIt = select.getJoins().iterator(); joinsIt.hasNext();) {
				Join right = (Join) joinsIt.next();
				JoinOperator left = new JoinOperator()
				JoinOperator join = new JoinOperator(projectLeft,plainSelect.getWhere(),leftTableNames,right.toString());
			}
		}
		

	}
	
	 private static Operator joinTables(PlainSelect plainSelect) {
//			controller connect to join 
			Table fromLeft = (Table) plainSelect.getFromItem();
			if(fromLeft!=null){
				Operator leftOperator;
//				= new SelectOperator(plainSelect.getWhere(), new ScanOperator(fromLeft.toString()));
				if( plainSelect.getJoins()!=null) {
					Operator rightOperator;
					for (Iterator joinsIt = plainSelect.getJoins().iterator(); joinsIt.hasNext();) {
						// to produced after WHERE result
						System.out.print("hrerere");
						JoinOperator join = new JoinOperator(left,plainSelect.getWhere(),leftTableNames,right.toString());
						left = join.dump();
						left.printTable();
					}
					JoinOperator join = new JoinOperator(leftOperator,rightOperator,plainSelect.getWhere());
				}
				
			}
			return null;
	 }

}
