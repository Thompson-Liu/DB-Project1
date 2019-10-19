/**
 * Extract the attributes for sorting the left child and sorting the right child
 */
package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

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

public class EvaluateJoin implements ExpressionVisitor {


	private String leftTableName;
	private String rightTableName;
	private HashMap<String,String> tableAlias;
	private ArrayList<String> joinAttributesLeft;
	private ArrayList<String> joinAttributesRight;
	private Expression expr;


	public EvaluateJoin(Expression whereExpr, String leftTableName, 
			String rightTableName,HashMap<String,String> tableAlias) {
		joinAttributesLeft = new ArrayList<String> ();
		joinAttributesRight = new ArrayList<String> ();
		
		// change the leftTableName to its alias if exist
		if(tableAlias.containsKey(leftTableName)) {
			leftTableName=tableAlias.get(leftTableName);}
		this.leftTableName = leftTableName;
		// change the rightTableName to its alias if exist
		if(tableAlias.containsKey(rightTableName)) {
			rightTableName=tableAlias.get(rightTableName);}
		this.rightTableName = rightTableName;
		
		this.expr=whereExpr;
		this.tableAlias = tableAlias;
		this.expr.accept(this);
	}
	
	/**
	 * 
	 * @return the joining attributes given leftTable, rightTable
	 */
	public ArrayList<String> getJoinAttributesLeft(){
		return joinAttributesLeft;
	}
	
	/**
	 * 
	 * @return the joining attributes given tableA, tableB
	 */
	public ArrayList<String> getJoinAttributesRight(){
		return joinAttributesRight;
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
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);

	}

	@Override
	public void visit(OrExpression arg0) {
	}

	@Override
	public void visit(Between arg0) {
	}

	/**
	 * add the attributes to lists of leftAttributes and rightAttributes
	 */
	@Override
	public void visit(EqualsTo arg0) {
		Expression left= arg0.getLeftExpression();
		Expression right = arg0.getRightExpression();
		if((left instanceof Column) && (right instanceof Column)) {
			Column Col1 = (Column) left;
			Column Col2 = (Column) right;
			
			String Col1Table = Col1.getTable().getName();
			// change the leftTableName to its alias if exist
			if(tableAlias.containsKey(Col1Table)) {
				Col1Table =tableAlias.get(Col1Table);}
			String Col2Table = Col2.getTable().getName();
			if(tableAlias.containsKey(Col2Table)) {
				Col2Table =tableAlias.get(Col2Table);}
			
			boolean order = (Col1Table==this.leftTableName) && Col2Table==this.rightTableName;
			boolean inverse = (Col1Table==this.rightTableName) && Col2Table==this.leftTableName;
			if(order) {
				this.joinAttributesLeft.add(Col1.getColumnName());
				this.joinAttributesRight.add(Col2.getColumnName());
			}
			else if (inverse) {
				this.joinAttributesLeft.add(Col2.getColumnName());
				this.joinAttributesRight.add(Col1.getColumnName());
			}
		}

	}

	@Override
	public void visit(GreaterThan arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(GreaterThanEquals arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(InExpression arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IsNullExpression arg0) {

	}

	@Override
	public void visit(LikeExpression arg0) {
	}

	@Override
	public void visit(MinorThan arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(MinorThanEquals arg0) {
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
	}

	@Override
	public void visit(WhenClause arg0) {
	}

	@Override
	public void visit(ExistsExpression arg0) {
	}

	@Override
	public void visit(AllComparisonExpression arg0) {
	}

	@Override
	public void visit(AnyComparisonExpression arg0) {
	}

	@Override
	public void visit(Concat arg0) {
	}

	@Override
	public void visit(Matches arg0) {
	}

	@Override
	public void visit(BitwiseAnd arg0) {
	}

	@Override
	public void visit(BitwiseOr arg0) {
	}

	@Override
	public void visit(BitwiseXor arg0) {
	}

}
