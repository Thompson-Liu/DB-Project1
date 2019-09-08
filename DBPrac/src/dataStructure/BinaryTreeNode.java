package dataStructure;

public class BinaryTreeNode<T> {

	   protected T element;
	   protected BinaryTreeNode<T> left, right;

	   //================================================================
	   //  Creates a new tree node with the specified data.
	   //================================================================
	   public BinaryTreeNode (T obj) 
	   {
	      element = obj;
	      left = null;
	      right = null;
	   }  // constructor BinaryTreeNode

	   public void value(T a) {
		   this.element = a;
	   }
	   
	   public void setLeft(BinaryTreeNode left) {
		   this.left = left;
	   }
	   
	   public void setRight(BinaryTreeNode right) {
		   this.right = right;
	   }
	   
	}  