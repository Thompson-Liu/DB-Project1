package dataStructure;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import operator.Operator;
import java.util.*;

public class TreeNode {
	private Operator Operator;
	private ArrayList<TreeNode> children;
	private Expression condition;
	
	public TreeNode(Operator op) {
		this.Operator = op;
		this.condition = new NullValue();
		this.children = new ArrayList<TreeNode>();
	}
	public TreeNode(Operator op,Expression expr) {
		this.Operator=op;
		this.condition = expr;
		this.children=new ArrayList<TreeNode> ();
	}
	
	public TreeNode (Operator op,Expression expr,ArrayList<TreeNode> children) {
		this.Operator =op;
		this.condition = expr;
		this.children=children;
	}
	
	public ArrayList<TreeNode> getChildren(){
		return this.children;
	}
	
	public void addChild(TreeNode tn) {
		this.children.add(tn);
	}
	
	public void setCondition(Expression  expr) {
		this.condition=expr;
	}
	
	public void addCondition(Expression cond) {
		this.condition = new AndExpression(this.condition,cond);
	}
	
	public void printOpTree() {
		System.out.print(this.Operator.toString());
		for (TreeNode op:children) {
			System.out.println(op.toString());
			op.printOpTree();
		}
		
	}

}
