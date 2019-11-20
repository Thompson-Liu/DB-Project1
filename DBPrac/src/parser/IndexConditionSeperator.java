
/**
 * Separate the expression with column listed and the rest
 */
package parser;

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

public class IndexConditionSeperator implements ExpressionVisitor {
	private String indexColumn;
	private Expression original;
	private int lowKey;
	private int highKey;
	private boolean flag;
	private String tableName;
	private boolean change= false;
	private boolean applyToAll= true;

	public IndexConditionSeperator(String tableName, String column, Expression expr) {
		original= expr;
		lowKey= Integer.MIN_VALUE;
		highKey= Integer.MAX_VALUE;
		this.indexColumn= column;
		this.tableName= tableName;

		original.accept(this);
	}

	public int getLowKey() {
		return lowKey;
	}

	public int getHighKey() {
		return highKey;
	}

	public boolean changed() {
		return change;
	}
	
	public Expression getResidual() {
		return original;
	}

	@Override
	public void visit(NullValue arg0) {
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

	}

	@Override
	public void visit(DateValue arg0) {

	}

	@Override
	public void visit(TimeValue arg0) {
	}

	@Override
	public void visit(TimestampValue arg0) {
	}

	@Override
	public void visit(Parenthesis arg0) {

	}

	@Override
	public void visit(StringValue arg0) {

	}

	@Override
	public void visit(Addition arg0) {

	}

	@Override
	public void visit(Division arg0) {

	}

	@Override
	public void visit(Multiplication arg0) {

	}

	@Override
	public void visit(Subtraction arg0) {

	}

	@Override
	public void visit(AndExpression arg0) {
		flag= false;

		arg0.getLeftExpression().accept(this);
		if (flag) {
			arg0.setLeftExpression(new NullValue());
			flag= false;
			change= true;
		}

		arg0.getRightExpression().accept(this);
		if (flag) {
			arg0.setRightExpression(new NullValue());
			flag= false;
			change= true;
		}
	}

	@Override
	public void visit(OrExpression arg0) {
		flag= false;
		arg0.getLeftExpression().accept(this);
		if (flag) {
			arg0.setLeftExpression(new NullValue());
			flag= false;
			change= true;
		}
		flag= false;
		arg0.getRightExpression().accept(this);
		if (flag) {
			arg0.setRightExpression(new NullValue());
			flag= false;
			change= true;
		}
	}

	@Override
	public void visit(Between arg0) {

	}

	/** helper function
	 * 
	 * @param left: left expression
	 * @param right: right expression
	 * @return */
	private Integer checkLeft(Expression left, Expression right) {
		if ((left instanceof Column) && (right instanceof DoubleValue || right instanceof LongValue)) {
			if (((Column) left).getColumnName().equals(indexColumn) &&
				((Column) left).getTable().getName().equals(tableName)) {
				if (right instanceof DoubleValue) { return (int) ((DoubleValue) right).getValue(); }
				return (int) ((LongValue) right).getValue();
			}
		}
		return null;
	}

	private Integer checkRight(Expression left, Expression right) {
		if ((left instanceof DoubleValue) && (right instanceof Column)) {
			if (((Column) right).getColumnName() == indexColumn &&
				(((Column) right).getTable().getName() == tableName)) {
				if (left instanceof DoubleValue) {
					return (int) ((DoubleValue) left).getValue();
				}

				else {
					return (int) ((LongValue) left).getValue();
				}
			}
		}
		return null;
	}

	@Override
	public void visit(EqualsTo arg0) {
		Expression left= arg0.getLeftExpression();
		Expression right= arg0.getRightExpression();
		Integer value;
		if ((value= checkLeft(left, right)) != null) {
			lowKey= Math.max(lowKey, value);
			highKey= Math.min(highKey, value);
			flag= true;
			change= true;

		} else if ((value= checkRight(left, right)) != null) {
			lowKey= Math.max(lowKey, value);
			highKey= Math.min(value, highKey);
			flag= true;
			change= true;
		} else {
			applyToAll= false;
		}
	}

	@Override
	public void visit(GreaterThan arg0) {
		Expression left= arg0.getLeftExpression();
		Expression right= arg0.getRightExpression();
		Integer value;
		if ((value= checkLeft(left, right)) != null) {
			lowKey= Math.max(lowKey, value + 1);
			flag= true;
			change= true;
		} else if ((value= checkRight(left, right)) != null) {
			highKey= Math.min(value - 1, highKey);
			flag= true;
			change= true;
		} else {
			applyToAll= false;
		}
	}

	@Override
	public void visit(GreaterThanEquals arg0) {
		Expression left= arg0.getLeftExpression();
		Expression right= arg0.getRightExpression();
		Integer value;
		if ((value= checkLeft(left, right)) != null) {
			lowKey= Math.max(lowKey, value);
			flag= true;
			change= true;
		} else if ((value= checkRight(left, right)) != null) {
			highKey= Math.min(value, highKey);
			flag= true;
			change= true;
		} else {
			applyToAll= false;
		}
	}

	@Override
	public void visit(InExpression arg0) {

	}

	@Override
	public void visit(IsNullExpression arg0) {

	}

	@Override
	public void visit(LikeExpression arg0) {
	}

	@Override
	public void visit(MinorThan arg0) {
		Expression left= arg0.getLeftExpression();
		Expression right= arg0.getRightExpression();
		Integer value;
		if ((value= checkLeft(left, right)) != null) {
			highKey= Math.min(value - 1, highKey);
			flag= true;
			change= true;
		} else if ((value= checkRight(left, right)) != null) {
			lowKey= Math.max(lowKey, value + 1);
			flag= true;
			change= true;
		} else {
			applyToAll= false;
		}
	}

	@Override
	public void visit(MinorThanEquals arg0) {
		Expression left= arg0.getLeftExpression();
		Expression right= arg0.getRightExpression();
		Integer value;
		if ((value= checkLeft(left, right)) != null) {
			highKey= Math.min(value, highKey);
			flag= true;
			change= true;
		} else if ((value= checkRight(left, right)) != null) {
			lowKey= Math.max(lowKey, value);
			flag= true;
			change= true;
		} else {
			applyToAll= false;
		}
	}

	@Override
	public void visit(NotEqualsTo arg0) {
	}

	@Override
	public void visit(Column arg0) {
	}

	@Override
	public void visit(SubSelect arg0) {

	}

	@Override
	public void visit(CaseExpression arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(WhenClause arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ExistsExpression arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(AllComparisonExpression arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(AnyComparisonExpression arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Concat arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Matches arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BitwiseAnd arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BitwiseOr arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BitwiseXor arg0) {
		// TODO Auto-generated method stub

	}

	public boolean applyAll() {
		// TODO Auto-generated method stub
		return applyToAll;
	}
}
