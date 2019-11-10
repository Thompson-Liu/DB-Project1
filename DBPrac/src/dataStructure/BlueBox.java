package dataStructure;

import java.util.ArrayList;
import java.util.List;

/** UnionFind elements Each element consists of a set of attributes and three numeric constraints:
 * lo, hi, eq. */
public class BlueBox {

	private int root;
	private List<String> attr;
	private int lo;
	private int hi;
	private int eq;

	public BlueBox(int root) {
		attr= new ArrayList<String>();
		this.root= root;
	}

	public int getRoot() {
		return root;
	}

	public int getLower() {
		return lo;
	}

	public int getUpper() {
		return hi;
	}

	public int getEqual() {
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
