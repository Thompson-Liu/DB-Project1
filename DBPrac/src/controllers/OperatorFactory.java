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
				return new ScanOperator(name);
			} else {
				return new SelectOperator(name, whereClause);
			}
		} else {
			if (whereEmpty) {
				return new ProjectOperator(new ScanOperator(name), name, selectItems);
			} else {
				return new ProjectOperator(new SelectOperator(name, whereClause), name, selectItems);
			}
		}

	}
	 private static DataTable joinTables(PlainSelect plainSelect) {
//			controller connect to join 
			Table fromLeft = (Table) plainSelect.getFromItem();
			if(fromLeft!=null && plainSelect.getJoins()!=null){
				SelectOperator selectLeft = new SelectOperator(fromLeft.getName(),plainSelect.getWhere());
				DataTable left = selectLeft.dump();
				ArrayList<String> leftTableNames = new ArrayList<String>();
				leftTableNames.add(left.getTableName());
				for (Iterator joinsIt = plainSelect.getJoins().iterator(); joinsIt.hasNext();) {
					Join right = (Join) joinsIt.next();
					// to produced after WHERE result
					System.out.print("hrerere");
					JoinOperator join = new JoinOperator(left,plainSelect.getWhere(),leftTableNames,right.toString());
					left = join.dump();
					left.printTable();
				}
				return left;
			}
			return null;
	 }

}
