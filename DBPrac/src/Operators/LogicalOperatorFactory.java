package Operators;

import java.util.ArrayList;
import java.util.List;

import dataStructure.UnionFind;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import parser.UnionFindGenerator;
import logicalOperators.DuplicateEliminationLogOp;
import logicalOperators.JoinLogOp;
import logicalOperators.LogicalOperator;
import logicalOperators.ProjectLogOp;
import logicalOperators.SelectLogOp;
import logicalOperators.SortLogOp;

/** produce the operator tree structure
 * takes in the plainselect
 *
 */
public class LogicalOperatorFactory {

	/** Generate query plan of operator tree based on the plainSelect clause
	 * perform select first, then join, the project. If the query contains an Order By, sort operator 
	 * will be the root, followed by the rest of the plan. If the query contains Distinct, it will be 
	 * the root.   
	 * 
	 * @param plainSelect
	 * @return operator
	 */
	public LogicalOperator generateQueryPlan(PlainSelect plainSelect) {
		
		String aliasName = "";
		String fromLeft = plainSelect.getFromItem().toString();
		Join firstJoin = new Join();
		firstJoin.setRightItem(plainSelect.getFromItem());
		
		List<Join> joinList = new ArrayList<Join>();
		joinList.add(firstJoin);
		
		LogicalOperator intOp;
		if (plainSelect.getJoins() != null) {
			joinList.addAll(plainSelect.getJoins());
			intOp = pushSelect(plainSelect, joinList);
		} else {
			if(plainSelect.getFromItem().getAlias()!=null) {
				aliasName = plainSelect.getFromItem().getAlias().toString();
				String tempTable = fromLeft.replace("AS " + aliasName, "").trim();
				intOp = new SelectLogOp(tempTable, aliasName, plainSelect.getWhere());
			} else {
				intOp = new SelectLogOp(fromLeft , "", plainSelect.getWhere());
			}
		}

		// check select clause
		List<SelectItem> selectItems= plainSelect.getSelectItems();
		if (!(selectItems.get(0) instanceof AllColumns)) {
			intOp= new ProjectLogOp(intOp, selectItems);
		}
		Distinct d= plainSelect.getDistinct();
		List<OrderByElement> tmpList= plainSelect.getOrderByElements();
		if (tmpList != null) {
			List<String> orderByList= new ArrayList<String>(tmpList.size());
			for (OrderByElement x : tmpList) {
				String[] nameCol = x.toString().split("\\.");
				String full = nameCol[0] + "." + nameCol[1];
				orderByList.add(full);  
			}
			intOp= new SortLogOp(intOp, orderByList);
			return (d == null) ? intOp : new DuplicateEliminationLogOp((SortLogOp) intOp);
		}
		return intOp;
	}

	private LogicalOperator pushSelect(PlainSelect plainSelect, List<Join> joinList) {
		// TODO Auto-generated method stub
		UnionFindGenerator ufGen = new UnionFindGenerator(plainSelect.getWhere());
		UnionFind uf = ufGen.getUnionFind();
		ArrayList<LogicalOperator> joinChildren = new ArrayList<LogicalOperator>();
		
		for(int i = 0; i < joinList.size(); ++i) {
			Join joinRel = joinList.get(i);
			
			String tempAlias = "";
			String tempTable = joinRel.getRightItem().toString();
			if(joinRel.getRightItem().getAlias()!=null) {
				tempAlias = joinRel.getRightItem().getAlias().toString();
				tempTable = tempTable.replace("AS " + tempAlias, "").trim();
			}
			
			// If there is a bluebox containing the table, push down the selection
			SelectLogOp select;
			if  (uf.find(tempTable) !=  null) {
				select = new SelectLogOp(tempTable, tempAlias, lowKey, highKey);
			} else {
				select = new SelectLogOp(tempTable, tempAlias, lowKey, highKey);
			}
			joinChildren.add(select);
		}
		JoinLogOp join = new JoinLogOp(joinChildren, ufGen.getResidualJoin());
		
		// Check if there's still some remaning selection to do
		if (ufGen.getResidualSelect() != null) {
			return new SelectLogOp(join);
		}
		return join;
	}
	
	/** helper function to iterate the joining tree
	 *   perform left-deep join, add the right most branch as select each time
	 *   start with the inner most join
	 * 
	 * @param plainSelect
	 * @param joins
	 * @return
	 */
//	private LogicalOperator join(PlainSelect plainSelect, List<Join> joins) {
//		if (joins.size() == 1) {
//			Join res= joins.get(0);
//			String tempAlias = "";
//			String tempTable = res.getRightItem().toString();
//			
//			if(res.getRightItem().getAlias() != null) {
//				tempAlias = res.getRightItem().getAlias().toString();
//				tempTable = tempTable.replace("AS " + tempAlias, "").trim();
//			} 
//			return new SelectLogOp(tempTable, tempAlias, plainSelect.getWhere());
//		}
//		Join rightJoin= joins.remove(joins.size() - 1);
//		Expression whereExp = plainSelect.getWhere();
//		String tempAlias = "";
//		String tempTable = rightJoin.getRightItem().toString();
//		if(rightJoin.getRightItem().getAlias()!=null) {
//			tempAlias = rightJoin.getRightItem().getAlias().toString();
//			tempTable = tempTable.replace("AS " + tempAlias, "").trim();
//		}
//		SelectLogOp rightOperator= new SelectLogOp(tempTable, tempAlias, whereExp);
//		return (new JoinLogOp(join(plainSelect, joins), rightOperator, whereExp));
//	}
}
