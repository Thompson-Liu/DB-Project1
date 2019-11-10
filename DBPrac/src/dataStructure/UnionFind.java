package dataStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UnionFind {

	// entry on the ith position in indlist is the root index of the cluster that col with ID i is in.
	private List<Integer> indList;
	private HashMap<String, Integer> colToInd; // A mapping from column names to column ids
	private HashMap<Integer, BlueBox> indToBox; // A mapping from column ids to blue boxes

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

	public void merge(BlueBox b1, BlueBox b2) {

	}

	public void setLower(BlueBox box, int lo) {
		indToBox.get(box.getRoot()).setLower(lo);
	}

	public void setUpper(BlueBox box, int up) {
		indToBox.get(box.getRoot()).setUpper(up);
	}

	public void setEqual(BlueBox box, int eq) {
		indToBox.get(box.getRoot()).setEqual(eq);
	}

}
