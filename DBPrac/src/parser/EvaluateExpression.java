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
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SubSelect;

public class EvaluateExpression implements ExpressionVisitor {

	private Stack<Integer> sofar;
	private Tuple dataTuple;
	private ArrayList<String> schema;
	private String tableName;		// Table name of the current tuple to be evaluated
	private Expression expression;

	public EvaluateExpression(String tableName,Expression exp) {
		sofar= new Stack<Integer>();
		schema= Catalog.getInstance().getSchema(tableName);
		this.tableName=tableName;
		expression = exp;
	}

	public Tuple evaluate(Tuple data) {
		this.dataTuple= data;
		expression.accept(this);
		if (sofar.size() == 0) { return dataTuple; }
		int result= sofar.pop();
		System.out.println(result);
		if (result == 1)
			return dataTuple;
		else
			return null;
	}

	@Override
	public void visit(NullValue arg0) {
		return;
	}

	@Override
	public void visit(Function arg0) {
		return;
	}

	@Override
	public void visit(InverseExpression arg0) {
		return;
	}

	@Override
	public void visit(JdbcParameter arg0) {
		return;
	}

	@Override
	public void visit(DoubleValue arg0) {
		return;
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
		Object right= sofar.pop();
		Object left= sofar.pop();
		if(right==null || left==null) {
			sofar.push(1);
		}
		else {
		sofar.push(((int)right == (int)left && (int)left == 1) ? 1 : 0);}
	}

	@Override
	public void visit(OrExpression arg0) {
		return;
	}

	@Override
	public void visit(Between arg0) {
		return;
	}

	@Override
	public void visit(EqualsTo arg0) {
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
		Object right= sofar.pop();
		Object left= sofar.pop();
		if(right==null || left==null) {
			sofar.push(1);
		}
		else {
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
		}
		else {
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
		}
		else {
		sofar.push(((int)left >= (int)right) ? 1 : 0);}
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
		if(right==null || left==null) {
			sofar.push(1);
		}
		else {
		sofar.add(((int)left < (int)right) ? 1 : 0);}
	}

	@Override
	public void visit(MinorThanEquals arg0) {
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
		Object right= sofar.pop();
		Object left= sofar.pop();
		if(right==null || left==null) {
			sofar.push(1);
		}
		else {
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
			sofar.add(((int)left != (int)right) ? 1 : 0);
		}
	}

	@Override
	public void visit(Column arg0) {
		if(arg0.getTable().getName()!=this.tableName) {
			sofar.push(null);
		}else {
			int index= schema.indexOf(arg0.getColumnName());
			sofar.push(dataTuple.getData(index));
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
