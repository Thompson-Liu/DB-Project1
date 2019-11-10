package dataStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** This class implements the union find algorithm. An instance is a UnionFind object containing
 * blue boxes generated from the queries. */
public class UnionFind {

	// entry on the ith position in indlist is the root index of the cluster that col with ID i is in.
	private List<Integer> indList;
	private HashMap<String, Integer> colToInd; // A mapping from column names to column ids
	private HashMap<Integer, BlueBox> indToBox; // A mapping from column ids to blue boxes

	/** Constructs a new UnionFind instance. */
	public UnionFind() {
		indList= new ArrayList<>();
		colToInd= new HashMap<>();
		indToBox= new HashMap<>();
	}

	private int findRoot(int ind) {
		while (ind != indList.get(ind)) {
			ind= indList.get(ind); // Root satisfies indList[root_index] = root_index b/c of how root is added
		}
		return ind;
	}

	/** Find the bluebox that col is in. If no such box exists, create a new bluebox containing col.
	 * 
	 * @param String col: the name of the column to look for
	 * @return a bluebox whose attribute list contains col */
	public BlueBox find(String col) {
		if (colToInd.containsKey(col)) {
			return indToBox.get(findRoot(colToInd.get(col))); // col already in a box so just find its root
		} else {
			// Create a new cluster (blue box)
			int pos= indList.size();
			BlueBox bb= new BlueBox(pos);
			bb.addAttr(col);
			// Keep invariants true
			indList.add(pos);
			colToInd.put(col, pos);
			indToBox.put(pos, bb);
			return bb;
		}
	}

	/** Merge two blue boxes. Modify the UnionFind data structure in place.
	 * 
	 * @param BlueBox b1, BlueBox b2: two blue boxes to be merged */
	public void merge(BlueBox b1, BlueBox b2) {
		// Keep invariants true
		int root1= b1.getRoot();
		indList.set(root1, b2.getRoot());
		indToBox.remove(root1);
		// Keep blue box constraints true
		if (b1.getLower() != null && b2.getLower() != null) {
			b2.setLower(Math.max(b1.getLower(), b2.getLower()));
		} else if (b2.getLower() == null) {
			b2.setLower(b1.getLower());
		}
		if (b1.getUpper() != null && b2.getUpper() != null) {
			b2.setUpper(Math.min(b1.getUpper(), b2.getUpper()));
		} else if (b2.getUpper() == null) {
			b2.setUpper(b1.getUpper());
		}
		if (b2.getEqual() == null) {
			b2.setEqual(b1.getEqual());
		}
		for (String attr : b1.getAttr()) {
			b2.addAttr(attr);
		}
	}

	/** Set the lower bound field for bluebox box to be lo
	 * 
	 * @param box: the bluebox to be reset
	 * @param lo: the new lower bound */
	public void setLower(BlueBox box, int lo) {
		indToBox.get(box.getRoot()).setLower(lo);
	}

	/** Set the upper bound field for bluebox box to be up
	 * 
	 * @param box: the bluebox to be reset
	 * @param up: the new upper bound */
	public void setUpper(BlueBox box, int up) {
		indToBox.get(box.getRoot()).setUpper(up);
	}

	/** Set the equality field for bluebox box to be eq
	 * 
	 * @param box: the bluebox to be reset
	 * @param eq: the new equality bound */
	public void setEqual(BlueBox box, int eq) {
		indToBox.get(box.getRoot()).setEqual(eq);
	}

}
