package dataStructure;


import java.util.*;
import logicalOperators.LogicalOperator;

public class TreeNode {
	private LogicalOperator operator;
	private ArrayList<TreeNode> children;
	
	public TreeNode(LogicalOperator op) {
		operator = op;
		children = new ArrayList<TreeNode>();
	}
	
	public TreeNode (LogicalOperator op, ArrayList<TreeNode> children) {
		this.operator =op;
		this.children=children;
	}
	
	public ArrayList<TreeNode> getChildren(){
		return children;
	}
	
	public void addChild(TreeNode tn) {
		children.add(tn);
	}

	public void printOpTree() {
		System.out.print(operator.toString());
		for (TreeNode op:children) {
			System.out.println(op.toString());
			op.printOpTree();
		}	
	}
}
