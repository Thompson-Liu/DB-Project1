package dataStructure;

import java.util.ArrayList;
import java.util.List;

/** UnionFind elements: Each element consists of a set of attributes and three numeric constraints:
 * lo, hi, eq. */
public class BlueBox {

	private int root;
	private List<String> attr;
	private Integer lo;
	private Integer hi;
	private Integer eq;

	public BlueBox(int root) {
		attr= new ArrayList<String>();
		this.root= root;
	}

	public int getRoot() {
		return root;
	}

	public Integer getLower() {
		return lo;
	}

	public Integer getUpper() {
		return hi;
	}

	public Integer getEqual() {
		return eq;
	}

	public List<String> getAttr() {
		return attr;
	}

	public void setLower(int newVal) {
		lo= newVal;
	}

	public void setUpper(int newVal) {
		hi= newVal;
	}

	public void setEqual(int newVal) {
		eq= newVal;
	}

	public void addAttr(String newAttr) {
		attr.add(newAttr);
	}

	public void setRoot(int newRoot) {
		root= newRoot;
	}

}
