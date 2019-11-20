package dataStructure;

import java.util.ArrayList;
import java.util.List;

/** UnionFind elements: Each element consists of a set of attributes and three numeric constraints:
 * lo, hi, eq. */
public class BlueBox {

	private List<String> attr;
	private Integer lo;
	private Integer hi;
	private Integer eq;

	public BlueBox(List<String> attribute) {
		attr= new ArrayList<String>();
		attr.addAll(attribute);
		lo = null;
		hi = null;
		eq = null;
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
		eq = newVal;
		lo = newVal;
		hi = newVal;
	}

	public void addAttr(String newAttr) {
		attr.add(newAttr);
	}
	
	public boolean equals(BlueBox box) {
		// check if working as desired
		return box.getAttr().equals(attr);	
	}

	public Integer[] getBound() {
		// TODO Auto-generated method stub
		return new Integer[] { lo, hi };
	}
}
