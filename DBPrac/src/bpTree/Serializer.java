/**
 * 
 */
package bpTree;

import java.util.ArrayList;
import java.util.Stack;

import fileIO.TupleReader;
import fileIO.TupleWriter;

public class Serializer {

	private TupleWriter bw;
	private Stack<ArrayList<Node>> levelsNodes;    				// store the levels from root to leaves
	private int totalnumNodes;
	private int order;

	public Serializer(boolean isClustered, TupleReader tr, TupleWriter bw,
			String attr, String tableName, String alias, int order) {
		this.bw= bw;
		this.order= order;
	}

	private void writeHeader(int rootAddress, int numLeaves, int order) {
		bw.reset(0);
		bw.addNextValue(rootAddress);
		bw.addNextValue(numLeaves);
		bw.addNextValue(order);
		bw.dump();
	}

	public void writeLeaveNode(LeafNode leaf) {
		bw.addNextValue(leaf.getType());
		bw.addNextValue(leaf.getNumElement());
		for (int i= 0; i < leaf.getNumElement(); i++ ) {
			int k= leaf.getKey(i);
			bw.addNextValue(k);
			ArrayList<int[]> rids= leaf.getRids(k);
			for (int[] rid : rids) {
				bw.addNextValue(rid[0]);
				bw.addNextValue(rid[1]);
			}
		}
		bw.dump();
	}

	public void writeIndexNode(IndexNode indexNode) {
		bw.addNextValue(indexNode.getType());
		bw.addNextValue(indexNode.getChildren().size());
		
		// write keys
		for (int key : indexNode.getKeys()) {
			bw.addNextValue(key);
		}
		
		// write addresses
		for (Node child : indexNode.getChildren()) {
			bw.addNextValue(child.getPage());
		}
		bw.dump();
	}


}
