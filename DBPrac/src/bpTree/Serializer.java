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
		levelsNodes= new Stack<ArrayList<Node>>();

		BulkLoader bulkLoading= new BulkLoader(isClustered, order, tr, attr, tableName, alias);
		collectNodes(bulkLoading.buildTree());
		writeHeader();
		writeLeaveNode();
		writeIndexNode();
	}

	// BFS to collect all nodes
	private void collectNodes(Node root) {
		ArrayList<Node> curLevel= new ArrayList<Node>();
		ArrayList<Node> nextLevel= new ArrayList<Node>();
		curLevel.add(root);
		totalnumNodes= 1;
		nextLevel= root.getChildren();

		while (nextLevel.size() != 0) {
			levelsNodes.add(curLevel);
			nextLevel= new ArrayList<Node>();
			for (Node curNode : curLevel) {
				ArrayList<Node> curChildren= curNode.getChildren();
				if (curChildren != null) {
					nextLevel.addAll(curChildren);
					totalnumNodes+= curChildren.size();
				}

			}
			curLevel= nextLevel;
//			levelsNodes.add(curLevel);
		}

	}

	private void writeHeader() {
		int numleaves= levelsNodes.get(levelsNodes.size() - 1).size();
		bw.addNextValue(numleaves);
		bw.addNextValue(totalnumNodes);
		bw.addNextValue(order);
		bw.dump();
	}

	private void writeLeaveNode() {
		for (Node temp : levelsNodes.get(levelsNodes.size() - 1)) {
			LeafNode leaf= (LeafNode) temp;
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

	}

	private void writeIndexNode() {
		for (int i= levelsNodes.size() - 2; i >= 0; i-- ) {
			ArrayList<Node> indexNodes= levelsNodes.get(i);
			for (int j= 0; j < indexNodes.size(); j++ ) {
//				System.out.println(indexNodes.get(j).getType());
				IndexNode indexNode= (IndexNode) indexNodes.get(j);
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
	}

}
