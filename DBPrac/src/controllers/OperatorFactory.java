package controllers;

import java.util.ArrayList;
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

		String fromLeft= plainSelect.getFromItem().toString();
		Operator intOp;
		Operator leftOp= new SelectOperator(plainSelect.getWhere(), new ScanOperator(fromLeft));

		if (plainSelect.getJoins() != null) {
			intOp= new JoinOperator(leftOp, join(plainSelect, plainSelect.getJoins()), plainSelect.getWhere());
		} else {
			intOp= leftOp;

		}

		// check select clause
		List<SelectItem> selectItems= plainSelect.getSelectItems();
		if (!(selectItems.get(0) instanceof AllColumns)) {
			intOp= new ProjectOperator(intOp, selectItems);
		}
		Distinct d= plainSelect.getDistinct();
		List<OrderByElement> tmpList= plainSelect.getOrderByElements();
		if (tmpList != null) {
			List<String> orderByList= new ArrayList<String>(tmpList.size());
			for (OrderByElement x : tmpList) {
				orderByList.add(x.toString().substring(x.toString().indexOf('.') + 1));
			}
			intOp= new SortOperator(intOp, orderByList);
			return (d == null) ? intOp : new DuplicateEliminationOperator((SortOperator) intOp);
		}
		if (d != null) {
//			System.out.println("Now, schema is" + intOp.schema());
			intOp= new SortOperator(intOp, null);
//			System.out.println("Next, schema is" + intOp.schema());
			return new DuplicateEliminationOperator((SortOperator) intOp);

		}
		return intOp;
	}

	private Operator join(PlainSelect plainSelect, List<Join> joins) {
		if (joins.size() == 1) {
			Operator scanOp= new ScanOperator(joins.get(0).toString());
			return new SelectOperator(plainSelect.getWhere(), scanOp);
		}
		String rightName= joins.remove(joins.size() - 1).toString();
		Expression whereExp= plainSelect.getWhere();
		SelectOperator rightOperator= new SelectOperator(whereExp, new ScanOperator(rightName));
		return (new JoinOperator(join(plainSelect, joins), rightOperator, whereExp));
	}

}
