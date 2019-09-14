package controllers;

import java.util.*;

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

		String name = ((Table) (select.getFromItem())).getName();
		boolean selectAll;
		boolean whereEmpty;
		
		// check where clause 
		Expression whereClause = select.getWhere();
		if (whereClause == null) { whereEmpty = true; } 
		else { whereEmpty = false; }
		
		// check select clause
		List<SelectItem> selectItems = select.getSelectItems();
		if (selectItems.get(0) instanceof AllColumns) {
			if (whereEmpty) { return new ScanOperator(name); }
			else { return new SelectOperator(name, whereClause); }
		} else {
			if (whereEmpty) { return new ProjectOperator(select, new ScanOperator(name), name, selectItems); }
			else { return new ProjectOperator(select, new SelectOperator(name, whereClause), name, selectItems); }
		}
		
		
	}

}
