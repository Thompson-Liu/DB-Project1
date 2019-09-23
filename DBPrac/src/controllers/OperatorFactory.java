package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import operator.DuplicateEliminationOperator;
import operator.JoinOperator;
import operator.Operator;
import operator.ProjectOperator;
import operator.ScanOperator;
import operator.SelectOperator;
import operator.SortOperator;

public class OperatorFactory {

	public Operator generateQueryPlan(PlainSelect plainSelect) {

		// alias ->tableName
		// (key: tableName, value: alias)
		HashMap<String, String> tableAlias= new HashMap<String, String>();
		String aliasName= "";

		String fromLeft= plainSelect.getFromItem().toString();

		if (plainSelect.getFromItem().getAlias() != null) {
			tableAlias.put(plainSelect.getFromItem().toString(),
				plainSelect.getFromItem().getAlias().toString());
			aliasName= plainSelect.getFromItem().getAlias().toString();
		}
		Operator intOp;
		Operator leftOp= new SelectOperator(plainSelect.getWhere(), new ScanOperator(fromLeft, aliasName), tableAlias);

		if (plainSelect.getJoins() != null) {
			intOp= new JoinOperator(leftOp, join(plainSelect, plainSelect.getJoins(), tableAlias),
				plainSelect.getWhere(), tableAlias);
		} else {
			intOp= leftOp;

		}

		// check select clause
		List<SelectItem> selectItems= plainSelect.getSelectItems();
		if (!(selectItems.get(0) instanceof AllColumns)) {
			intOp= new ProjectOperator(intOp, selectItems, tableAlias);
		}
		Distinct d= plainSelect.getDistinct();
		List<OrderByElement> tmpList= plainSelect.getOrderByElements();
		if (tmpList != null) {
			List<String> orderByList= new ArrayList<String>(tmpList.size());
			for (OrderByElement x : tmpList) {
				orderByList.add(x.toString());   // .substring(x.toString().indexOf('.') + 1));
			}
			intOp= new SortOperator(intOp, orderByList);
			return (d == null) ? intOp : new DuplicateEliminationOperator((SortOperator) intOp);
		}
		if (d != null) {
			intOp= new SortOperator(intOp, null);
			return new DuplicateEliminationOperator((SortOperator) intOp);

		}
		return intOp;
	}

	private Operator join(PlainSelect plainSelect, List<Join> joins, HashMap<String, String> tableAlias) {
		if (joins.size() == 1) {
			Join res= joins.get(0);
			Operator scanOp;
			if (res.getRightItem().getAlias() != null) {
				scanOp= new ScanOperator(res.getRightItem().toString(), res.getRightItem().getAlias().toString());
			} else {
				scanOp= new ScanOperator(res.getRightItem().toString(), "");
			}

			return new SelectOperator(plainSelect.getWhere(), scanOp, tableAlias);
		}
		Join rightJoin= joins.remove(joins.size() - 1);
		Expression whereExp= plainSelect.getWhere();
		Operator rightOp;
		if (rightJoin.getRightItem().getAlias() != null) {
			tableAlias.put(rightJoin.getRightItem().toString(), rightJoin.getRightItem().getAlias());
			rightOp= new ScanOperator(rightJoin.toString(), rightJoin.getRightItem().getAlias().toString());
		} else {
			rightOp= new ScanOperator(rightJoin.toString(), "");
		}
		SelectOperator rightOperator= new SelectOperator(whereExp, rightOp, tableAlias);
		return (new JoinOperator(join(plainSelect, joins, tableAlias), rightOperator, whereExp, tableAlias));
	}

}
