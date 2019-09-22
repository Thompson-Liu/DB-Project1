/**
 * Evaluate the WHERE clause that involves two tables, keep a stack structure that evaluate the expression tree
 *            AND                             
 * 		   /       \        
 * 	       <      exp2
 *       /   \
 *     1     2
 * 
 */
package parser;

import java.util.ArrayList;
import java.util.Stack;

import dataStructure.Catalog;
import dataStructure.Tuple;
import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.InverseExpression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

public class EvaluateWhere implements ExpressionVisitor {

	private Stack<Integer> sofar;
	private Tuple resultTuple;
	private Tuple leftTuple;
	private Tuple rightTuple;
	private ArrayList<String> leftSchema = new ArrayList<String>();
	private ArrayList<String> rightSchema = new ArrayList<String>();
//	private String leftTableNames;
//	private String rightTableNames;
	private Expression expr;


	public EvaluateWhere(Expression whereExpr, 
			ArrayList<String> leftSchema, ArrayList<String> rightSchema) {
		this.expr=whereExpr;
		this.leftSchema = leftSchema;
		this.rightSchema = rightSchema;
//		leftTableNames = leftTables;
//		rightTableNames=rightTables;
	}

	//	// compute the schema for this tables sets
	//	private void initSchema() {
	//		for(String tableName: this.leftTupleTables) {
	//			this.leftSchema.addAll(Catalog.getInstance().getSchema(tableName));
	//		}
	//		this.rightSchema= Catalog.getInstance().getSchema(this.rightTupleTable);
	//	}
	

	public Tuple evaluate(Tuple leftTuple, Tuple rightTuple) {
		sofar= new Stack<Integer>();
		this.leftTuple = leftTuple;
		this.rightTuple = rightTuple;
		if(expr==null) {
			sofar.add(1);
		}else {
			expr.accept(this);
		}
		if (leftTuple != null) {
			resultTuple = leftTuple;
			resultTuple.concateTuple(rightTuple);
		} else {
			resultTuple = rightTuple;
		}

		if(sofar.size()==0) {
			return resultTuple;
		}
		else if (sofar.pop() == 1)
			return resultTuple;
		else
			return null;
	}

	@Override
	public void visit(NullValue arg0) {
		sofar.add(1);
	}

	@Override
	public void visit(Function arg0) {
	}

	@Override
	public void visit(InverseExpression arg0) {
	}

	@Override
	public void visit(JdbcParameter arg0) {
	}

	@Override
	public void visit(DoubleValue arg0) {
	}

	@Override
	public void visit(LongValue arg0) {
		sofar.push((int) (arg0.getValue()));
	}

	@Override
	public void visit(DateValue arg0) {
		return;
	}

	@Override
	public void visit(TimeValue arg0) {
		return;
	}

	@Override
	public void visit(TimestampValue arg0) {
		return;
	}

	@Override
	public void visit(Parenthesis arg0) {
		return;
	}

	@Override
	public void visit(StringValue arg0) {
		return;
	}

	@Override
	public void visit(Addition arg0) {
		return;
	}

	@Override
	public void visit(Division arg0) {
		return;
	}

	@Override
	public void visit(Multiplication arg0) {
		return;
	}

	@Override
	public void visit(Subtraction arg0) {
		return;
	}

	@Override
	public void visit(AndExpression arg0) {
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
		int right= sofar.pop();
		int left= sofar.pop();
		sofar.push((right == left && left == 1) ? 1 : 0);
	}

	@Override
	public void visit(OrExpression arg0) {
		return;
	}

	@Override
	public void visit(Between arg0) {
		return;
	}

	/**
	 * @param arg0   EqualTo expression composed of leftExp and rightExp
	 * @return value  0 left != right;   
	 * 				1  if left == right  or should ignore this expression, 
	 * 								since these evaluation does not apply to these two tuple
	 * e.g.  tuple1 from tableA, tuple2 from tableB, with expression C.x==2 
	 *  	push[1] to the stack [sofar], since left will give null expression
	 */
	@Override
	public void visit(EqualsTo arg0) {
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
		Object right= sofar.pop();
		Object left= sofar.pop();
		if(right==null || left==null) {
			sofar.push(1);
		}else {
			sofar.push(((int)left == (int)right) ? 1 : 0);}
	}

	@Override
	public void visit(GreaterThan arg0) {
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
		Object right= sofar.pop();
		Object left= sofar.pop();
		if(right==null || left==null) {
			sofar.push(1);
		}else {
			sofar.push(((int)left > (int)right) ? 1 : 0);}
	}

	@Override
	public void visit(GreaterThanEquals arg0) {
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
		Object right= sofar.pop();
		Object left= sofar.pop();
		if(right==null || left==null) {
			sofar.push(1);
		}else {
			sofar.push(((int)left >= (int) right) ? 1 : 0);}
	}

	@Override
	public void visit(InExpression arg0) {
		return;
	}

	@Override
	public void visit(IsNullExpression arg0) {
		return;
	}

	@Override
	public void visit(LikeExpression arg0) {
		return;
	}

	@Override
	public void visit(MinorThan arg0) {
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
		Object right= sofar.pop();
		Object left= sofar.pop();
		if(left==null || right==null) {
			sofar.push(1);
		}else {
			sofar.push(((int)left < (int)right) ? 1 : 0);}
	}

	@Override
	public void visit(MinorThanEquals arg0) {
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
		Object right= sofar.pop();
		Object left= sofar.pop();
		if(left==null || right ==null) {
			sofar.push(1);
		}else {
			sofar.add(((int)left <= (int)right) ? 1 : 0);}
	}

	@Override
	public void visit(NotEqualsTo arg0) {
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
		Object right= sofar.pop();
		Object left= sofar.pop();
		if(right==null || left==null) {
			sofar.push(1);
		}
		else {
			sofar.push(((int)left != (int)right) ? 1 : 0);}
	}

	/**
	 * @param   column expression
	 * @return void
	 * 
	 */
	@Override
	public void visit(Column arg0) {
		String colTable = arg0.getTable().getName();
		String colName = arg0.getColumnName();
		String colInfo = colTable+"."+colName;      // the new name of the column  e.g. Sailor.A
		
		if(!leftSchema.contains(colInfo) && !rightSchema.contains(colInfo)) {
			sofar.push(null);
		}else {
			if (leftSchema.contains(colInfo) ) {
					int index= leftSchema.indexOf(colInfo);
					sofar.push(leftTuple.getData(index));
			}
			else  {
					int index= rightSchema.indexOf(colInfo);
					sofar.push(rightTuple.getData(index));
			}
		}
	}

	@Override
	public void visit(SubSelect arg0) {
		return;
	}

	@Override
	public void visit(CaseExpression arg0) {
		return;
	}

	@Override
	public void visit(WhenClause arg0) {
		return;
	}

	@Override
	public void visit(ExistsExpression arg0) {
		return;
	}

	@Override
	public void visit(AllComparisonExpression arg0) {
		return;
	}

	@Override
	public void visit(AnyComparisonExpression arg0) {
		return;
	}

	@Override
	public void visit(Concat arg0) {
		return;
	}

	@Override
	public void visit(Matches arg0) {
		return;
	}

	@Override
	public void visit(BitwiseAnd arg0) {
		return;
	}

	@Override
	public void visit(BitwiseOr arg0) {
		return;
	}

	@Override
	public void visit(BitwiseXor arg0) {
		return;
	}

}
