package bpTree;

import java.util.ArrayList;

import dataStructure.Catalog;
import dataStructure.Tuple;
import fileIO.BinaryTupleWriter;
import fileIO.TupleReader;
import fileIO.TupleWriter;

public class BulkLoader {

	private int order;
	private TupleReader tr;
	private String attr;
	private String tableName;
	int counter = 1;
	private Catalog catalog;

	private void generateSorted(boolean isClustered) {
		if (!isClustered) {
			TupleWriter writer = new BinaryTupleWriter(tr.getFileInfo());
			Tuple tup;
			while ((tup = tr.readNextTuple()) != null) {
				writer.addNextTuple(tup);
			}
			writer.dump();
			writer.close();
		}
	}

	public BulkLoader(boolean isClustered, int order, TupleReader tr, String attr, String tableName) {
		this.order = order;
		this.tr = tr;
		catalog = Catalog.getInstance();
		this.tableName = tableName;
		this.attr = attr;
		generateSorted(isClustered);
	}

	public Node buildTree() {
		ArrayList<Node> leaves = buildLeaves();

		ArrayList<IndexNode> indices = new ArrayList<IndexNode>();
		// Generate upper level of index nodes
		do {
			indices = buildIndex(leaves);
		} while(indices.size() > 1);
		return indices.get(0);
	}

	private ArrayList<Node> buildLeaves(){
		ArrayList<Node> leaves= new ArrayList<Node>();
		int curKey = Integer.MIN_VALUE;
		Tuple tp;

		while ((tp = tr.readNextTuple()) != null) {
			int numKeys=0;
			Node leaf = new LeafNode(counter++);

			while(numKeys < order * 2) {
				int colNum = catalog.getSchema(tableName).indexOf(attr);
				int key = tp.getData(colNum);
				int[] rid = new int[] { tr.getPage(), tr.getCurRow() };
				((LeafNode)leaf).add(key,rid);
				if(key != curKey) {
					numKeys++;
					curKey = key;
				}
			}
			leaves.add(leaf);
		}
		tr.close();

		// Check if the last Node has less than d entries
		if (leaves.get(leaves.size() - 1).getNumElement() < order) {

			// check if there's only one index node
			if (leaves.size() > 1) {
				Node secLast = leaves.get(leaves.size() - 2);
				Node last = leaves.get(leaves.size() - 1);

				int totalElem = last.getNumElement() + secLast.getNumElement();

				// Regenerate the second to last node
				LeafNode secLastLeaf = new LeafNode(counter - 2);
				for (int i = 0; i < totalElem / 2; ++i) {
					int key = ((LeafNode)secLast).getKey(i);
					ArrayList<int[]> rids = ((LeafNode)secLast).getRids(key);
					secLastLeaf.addDatas(key, rids);
				}
				leaves.set(leaves.size() - 2, secLastLeaf);


				// Generate the last node of size [totalElem / 2] + size of original 
				LeafNode lastLeaf = new LeafNode(counter - 1);
				for (int i = totalElem / 2; i < secLast.getNumElement(); ++i) {
					int key = ((LeafNode)secLast).getKey(i);
					ArrayList<int[]> rids = ((LeafNode)secLast).getRids(key);
					secLastLeaf.addDatas(key, rids);
				}

				for (int i = 0; i < last.getNumElement(); ++i) {
					int key = ((LeafNode)secLast).getKey(i);
					ArrayList<int[]> rids = ((LeafNode)secLast).getRids(key);
					secLastLeaf.addDatas(key, rids);
				}
				leaves.set(leaves.size() - 1, lastLeaf);
			}
		}
		return leaves;
	}

	private ArrayList<IndexNode> buildIndex(ArrayList<Node> child) {
		// Generate the first level of index nodes
		ArrayList<IndexNode> indices = new ArrayList<IndexNode>();
		int numNodes = child.size() / (2 * order + 1);

		for (int i = 0; i < numNodes - 1; ++i) {
			IndexNode index = new IndexNode(counter++);
			for (int j = 0; j < 2 * order + 1; ++j) {
				index.addChild(child.get(i * (2 * order + 1) + j));
			}
			index.buildKeys();
			indices.add(index);
		}

		// Seperate the cases:
		int childRemain = child.size() - (2 * order + 1) * (numNodes - 1);

		// 1) Only one index node will be enough, ie. remain <= 2d + 1
		if (childRemain <= (2 * order + 1)) {
			IndexNode index = new IndexNode(counter++);
			for (int j = 0; j < childRemain; ++j) {
				index.addChild(child.get((numNodes - 1) * (2 * order + 1) + j));
			}
			index.buildKeys();
			indices.add(index);
		}

		// 2) Two indices are needed
		else {
			// Construct the second to last index
			IndexNode secLastIndex = new IndexNode(counter++);
			int startIndex = (numNodes - 1) * (2 * order + 1);
			int endIndex = childRemain >= 3 * order + 2 ? 
					startIndex + (2 * order + 1) : startIndex + childRemain / 2;
			for (int j = startIndex; j < endIndex; ++j) {
				secLastIndex.addChild(child.get(j));
			}
			secLastIndex.buildKeys();
			indices.add(secLastIndex);

			// construct the last index
			IndexNode lastIndex = new IndexNode(counter++);
			for (int j = endIndex; j < child.size(); ++j) {
				lastIndex.addChild(child.get(j));
			}
			lastIndex.buildKeys();
			indices.add(lastIndex);
		}
		return indices;
	}
}
