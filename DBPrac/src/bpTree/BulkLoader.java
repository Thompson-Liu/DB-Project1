package bpTree;

import java.util.ArrayList;

import dataStructure.Tuple;
import fileIO.TupleReader;

public class BulkLoader {

	private boolean isClustered;
	private int order;
	private Node root;
	private TupleReader tr;
	private String attr;
	private ArrayList<String> schema;
	int counter = 1;

	private void generateSorted() {

	}

	public BulkLoader(boolean isClustered, int order,TupleReader tr, String attr, ArrayList<String> schema) {
		if (!isClustered) {

		}


	}

	public Node buildTree() {
		ArrayList<Node> leaves = buildLeaves();

		ArrayList<Node> indices;
		do {
			indices = buildIndex();
		} while(indices.size() > 1);	
		
		return indices.get(0);
	}

	private ArrayList<Node> buildLeaves(){
		ArrayList<Node> leaves=new ArrayList<Node>();
		int curKey = Integer.MIN_VALUE;
		Tuple tp;

		while ((tp = tr.readNextTuple()) != null) {
			int numKeys=0;
			LeafNode leaf = new LeafNode(counter++);

			while(numKeys<order*2) {
				int colNum = schema.indexOf(attr);
				int key = tp.getData(colNum);
				int[] rid = new int[] { tr.getPage(), tr.getCurRow() };
				leaf.add(key,rid);
				if(key != curKey) {
					numKeys++;
					curKey = key;
				}

			}
			leaves.add(leaf);
		}

		// Check if the last Node has less than d entries
		if (leaves.get(leaves.size() - 1).getNumElement() < order) {

		}
		return leaves;

	}

	private ArrayList<Node> buildIndex() {
		while ()
			int count = 0;
		IndexNode index = new IndexNode(counter++);
		while(count < 2 * order) {
			IndexNode.
		}
		return null;
	}
}
