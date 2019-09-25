/** logic detailed explain:
 * 
 *  Evaluate the WHERE clause that involves two tables, keep a stack structure that evaluate the expression tree
 *  
 *  In the logic for evaluate where, we keep a stack structure of integer [sofar], that accumulate the result, 
 *  as we only have AND between conditions, we will push the number value if it were longvalue or int from column expression
 *  and push them to the stack [sofar], and when hiting the binary comparison, the number values will be pop out LIFO, and push
 *  the boolean of comparing result.
 *  
 *  This evaluateWhere can take in all the condition in where clause even when evaluating a single table, since
 *  it will push null to the stack if the table column does not match with the tuple's schema. When the binary expression
 *  of compare pop out null value it will push [1] which represents true to the stack [sofar]. This way each time
 *  we can input all the where clause and this evaluate visitor will be able to handle it
 *  
 *  For example, in the below join condition we have (T1.A<T2.B AND T3.C=3), we will perform deep-left join and 
 *  first evaluate SELECT T3.C and pass the whole where clause. Since tuple of T3, does not match with T1.A or T2.B,
 *  it will push null to the stack when visit them, and during binary operation of (<), left and right expressions are null,
 *  so it will push [1] true to the stack. This will not affect the evaluation of (3=T3.C), which is what we want to evaluate.
 *  
 *              AND                             
 * 		     /       \        
 * 	       <           =
 *       /  \         /  \
 *    T1.A  T2.B     3    T3.C
 *  
 */
package parser;

import java.util.ArrayList;
import java.util.HashMap;
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
	private HashMap<String,String> tableAlias;
//	private String leftTableNames;
//	private String rightTableNames;
	private Expression expr;


	public EvaluateWhere(Expression whereExpr, ArrayList<String> leftSchema, 
			ArrayList<String> rightSchema,HashMap<String,String> tableAlias) {
		this.expr=whereExpr;
		this.leftSchema = leftSchema;
		this.rightSchema = rightSchema;
		this.tableAlias = tableAlias;
	}



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
			resultTuple=resultTuple.concateTuple(rightTuple);
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

	/** Add 1 to the stack [sofar] if true, add 0 if false
	 * @param arg0 the andExpression to evaluate
	 */
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
	 * @return void push 0 to stack [sofar] if left != right;   
	 * 				     1  if left == right  or should ignore this expression, 
	 * 				     since these evaluation does not apply to these two tuple
	 *                   (i.e. the tuple does not belong to this schema)
	 * e.g.  tuple1 from tableA, tuple2 from tableB; with the expression: tableC.x==2 
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

	/** act similar to equalsTo
	 * @param arg0   GreaterThan expression
	 */
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

	/** push the 
	 * @param   column expression
	 * @return void
	 * 
	 */
	@Override
	public void visit(Column arg0) {
		String colTable = arg0.getTable().getName();
		
		//always using Alias as indexing
		if(tableAlias.containsKey(colTable)) {
			colTable=tableAlias.get(colTable);
		}
		
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
