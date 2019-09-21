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

	public Operator generateQueryPlan(PlainSelect plainSelect) {

		Operator resultOp;
		String fromLeft = plainSelect.getFromItem().toString();
		Operator intOp;
		Operator leftOp = new SelectOperator(plainSelect.getWhere(),new ScanOperator(fromLeft));
		
		if(plainSelect.getJoins() != null) {
			intOp= new JoinOperator(leftOp,join(plainSelect,plainSelect.getJoins()),plainSelect.getWhere());
		}else {
			intOp= leftOp;

		}


		// check select clause
		List<SelectItem> selectItems= plainSelect.getSelectItems();
		if (selectItems.get(0) instanceof AllColumns) {
			return intOp;
		} else {
			return new ProjectOperator(intOp, selectItems);

		}
	}

	private Operator join(PlainSelect plainSelect,List<Join> joins) {
		if (joins.size()==1) {
			Operator scanOp = new ScanOperator(joins.get(0).toString());
			return new SelectOperator(plainSelect.getWhere(), scanOp);
		}
		String rightName=joins.remove(joins.size()-1).toString();
		Expression whereExp = plainSelect.getWhere();
		SelectOperator rightOperator = new SelectOperator(whereExp, new ScanOperator(rightName));
		return(new JoinOperator(join(plainSelect,joins),rightOperator,whereExp));
	}

}
