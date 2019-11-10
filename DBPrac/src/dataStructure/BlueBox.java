package dataStructure;

import java.util.ArrayList;
import java.util.List;

/** UnionFind elements Each element consists of a set of attributes and three numeric constraints:
 * lo, hi, eq. */
public class BlueBox {

	private List<String> attr;
	private int lo;
	private int hi;
	private int eq;

	public BlueBox() {
		attr= new ArrayList<String>();
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

	public void setLower(int newVal) {
		lo= newVal;
	}

	public void setUpper(int newVal) {
		hi= newVal;
	}

	public void setEqual(int newVal) {
		eq= newVal;
	}

}
