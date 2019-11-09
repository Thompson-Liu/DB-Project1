
package bpTree;

import java.util.ArrayList;

import fileIO.TupleWriter;

/** The class that implements the serialization of a tree. */
public class Serializer {

	private TupleWriter bw;

	/** The serializer produces a serialized B+-tree in an index file.
	 * 
	 * @param bw: a TupleWriter */
	public Serializer(TupleWriter bw) {
		this.bw= bw;
	}

	public void writeHeader(int rootAddress, int numLeaves, int order) {
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

			// write the value of k
			int k= leaf.getKey(i);
			bw.addNextValue(k);

			// write the number of rids in the entry
			ArrayList<int[]> rids= leaf.getRids(k);
			bw.addNextValue(rids.size());

			// write pi and ti for each rid in the entry
			for (int[] rid : rids) {
				bw.addNextValue(rid[0]);
				bw.addNextValue(rid[1]);
			}
		}
		bw.dump();
	}

	public void writeIndexNode(IndexNode indexNode) {
		bw.addNextValue(indexNode.getType());
		bw.addNextValue(indexNode.getNumElement());

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
