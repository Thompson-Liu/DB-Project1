package bpTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import dataStructure.Catalog;
import dataStructure.Tuple;
import fileIO.BinaryTupleReader;
import fileIO.BinaryTupleWriter;
import fileIO.TupleReader;
import fileIO.TupleWriter;
import physicalOperator.ScanOperator;
import physicalOperator.SortOperator;

public class BulkLoader {

	private int order;
	private TupleReader tr;
	private TupleWriter tw;
	private String attr;
	private String tableName;
	private int counter = 1;
	private ArrayList<Integer> keys;
	private HashMap<Integer, ArrayList<int[]>> dataEntries;

	private Serializer serialize;

	private void generateSorted(int isClustered) {
		if (isClustered == 1) {
			ArrayList<String> sortList= new ArrayList<String>();
			sortList.add(tableName + "." + attr);

			SortOperator sort= new SortOperator(new ScanOperator(tableName, ""), sortList);
			TupleWriter writer= new BinaryTupleWriter(tr.getFileInfo());

			Tuple tup;
			while ((tup= sort.getNextTuple()) != null) {
				writer.addNextTuple(tup);
			}
			tr= new BinaryTupleReader(tr.getFileInfo());
			writer.dump();
			writer.close();
		}
	}

	public BulkLoader(int isClustered, int order, TupleReader tr, TupleWriter tw, String attr, String tableName) {
		this.order= order;
		this.tr= tr;
		this.tw = tw;
		this.tableName= tableName;
		this.attr= attr;
		generateSorted(isClustered);

		keys = new ArrayList<Integer>();
		dataEntries = new HashMap<Integer, ArrayList<int[]>>();

		serialize = new Serializer(tw);
	}

	public void buildTree() {
		buildDataEntries();
		ArrayList<Node> leaves = buildLeaves();
		int numLeaves = leaves.size();

		// Generate levels of index nodes until the number of index nodes becomes zero
		ArrayList<Node> indices= new ArrayList<Node>();
		do {
			indices = buildIndex(leaves);
			leaves = indices;
		} while (indices.size() > 1);

		Node root = indices.get(0);

		serialize.writeHeader(root.getPage(), numLeaves, order);
	}

	private void buildDataEntries() {
		Catalog catalog = Catalog.getInstance();
		int colNum = catalog.getSchema(tableName).indexOf(attr);

		Tuple tup;
		while ((tup = tr.readNextTuple()) != null) {
			int key = tup.getData(colNum);
			int[] rid = new int[] { tr.getTupleLoc()[0], tr.getTupleLoc()[1] };

			if (keys.contains(key)) {
				dataEntries.get(key).add(rid);
			} else {
				keys.add(key);
				ArrayList<int[]> dataEntry = new ArrayList<int[]>();
				dataEntry.add(rid);
				dataEntries.put(key, dataEntry);
			}
		}
		tr.close();
		Collections.sort(keys);
	}

	private ArrayList<Node> buildLeaves() {
		tw.reset(1);

		int numEntries = keys.size();
		int numNodes = (int) Math.ceil(numEntries * 1.0 / (2 * order));
		ArrayList<Node> leaves = new ArrayList<Node>(numNodes);

		// Insert all the data entries to generate (size - 2) leaf nodes
		for (int i = 0; i < numNodes - 2; ++i) {
			Node leaf= new LeafNode(counter++);

			for (int j = 0; j < 2 * order; ++j) {
				int key = keys.get(i * 2 * order + j);
				((LeafNode) leaf).addDatas(key, dataEntries.get(key));
			}
			leaves.add(leaf);
			serialize.writeLeaveNode( (LeafNode) leaf);
		}

		// If there is only one leaf node
		if (numNodes == 1) {
			Node leaf = new LeafNode(counter++);
			for (int j = 0; j < numEntries; ++j) {
				int key = keys.get(j);
				((LeafNode) leaf).addDatas(key, dataEntries.get(key));
			}
			leaves.add(leaf);
			serialize.writeLeaveNode( (LeafNode) leaf);
		} 

		else {
			// Check if the last node only has less than d data entries 
			if (numEntries % (2 * order) < order) {

				// Generate the second to last leaf node
				Node secLastLeaf = new LeafNode(counter++);
				int secLastNumEntry = (2 * order + numEntries % (2 * order)) / 2;

				for (int j = 0; j < secLastNumEntry; ++j) {
					int key = keys.get((numNodes - 2) * 2 * order + j);
					((LeafNode) secLastLeaf).addDatas(key, dataEntries.get(key));
				}
				leaves.add(secLastLeaf);
				serialize.writeLeaveNode( (LeafNode) secLastLeaf);

				// Generate the last leaf node
				Node lastLeaf = new LeafNode(counter++);
				int lastNumEntry = 2 * order + numEntries % (2 * order) - secLastNumEntry;

				for (int j = 0; j < lastNumEntry; ++j) {
					int key = keys.get(numEntries - lastNumEntry + j);
					((LeafNode) lastLeaf).addDatas(key, dataEntries.get(key));
				}
				leaves.add(lastLeaf);
				serialize.writeLeaveNode( (LeafNode) lastLeaf);
			} else {
				// Generate the second to last leaf node
				Node secLastLeaf = new LeafNode(counter++);

				for (int j = 0; j < 2 * order; ++j) {
					int key = keys.get((numNodes - 2) * 2 * order + j);
					((LeafNode) secLastLeaf).addDatas(key, dataEntries.get(key));
				}
				leaves.add(secLastLeaf);
				serialize.writeLeaveNode( (LeafNode) secLastLeaf);

				// Generate the last leaf node
				Node lastLeaf = new LeafNode(counter++);
				int lastNumEntry = numEntries - (numNodes - 1) * 2 * order;
				assert(lastNumEntry >= order);

				for (int j = 0; j < lastNumEntry; ++j) {
					int key = keys.get(numEntries - lastNumEntry + j);
					((LeafNode) lastLeaf).addDatas(key, dataEntries.get(key));
				}
				leaves.add(lastLeaf);
				serialize.writeLeaveNode( (LeafNode) lastLeaf);
			}
		}
		return leaves;
	}

	private ArrayList<Node> buildIndex(ArrayList<Node> child) {
		// Generate the first level of index nodes
		ArrayList<Node> indices= new ArrayList<Node>();
		int numNodes= child.size() / (2 * order + 1);

		for (int i= 0; i < numNodes - 1; ++i) {
			IndexNode index= new IndexNode(counter++ );
			for (int j= 0; j < 2 * order + 1; ++j) {
				index.addChild(child.get(i * (2 * order + 1) + j));
			}
			index.buildKeys();
			indices.add(index);
			serialize.writeIndexNode(index);
		}

		// Seperate the cases: if numNodes == 0, previous for loop does not execute
		// Otherwise, calculate the leftover nodes after executing the for loop
		int childRemain= numNodes == 0 ? child.size() : child.size() - (2 * order + 1) * (numNodes - 1);
		int multiplier= numNodes == 0 ? 0 : numNodes - 1;

		// 1) Only one index node will be enough, ie. remain <= 2d + 1
		if (childRemain <= (2 * order + 1)) {
			IndexNode index= new IndexNode(counter++);

			for (int j= 0; j < childRemain; ++j) {
				index.addChild(child.get(multiplier * (2 * order + 1) + j));
			}
			index.buildKeys();
			indices.add(index);
			serialize.writeIndexNode(index);
		}

		// 2) Two indices are needed
		else {
			// Construct the second to last index
			IndexNode secLastIndex= new IndexNode(counter++);
			int startIndex= multiplier * (2 * order + 1);
			int endIndex= childRemain >= 3 * order + 2 ? startIndex + (2 * order + 1) : startIndex + childRemain / 2;
			for (int j= startIndex; j < endIndex; ++j) {
				secLastIndex.addChild(child.get(j));
			}
			secLastIndex.buildKeys();
			indices.add(secLastIndex);
			serialize.writeIndexNode(secLastIndex);

			// construct the last index
			IndexNode lastIndex= new IndexNode(counter++ );
			for (int j= endIndex; j < child.size(); ++j) {
				lastIndex.addChild(child.get(j));
			}
			lastIndex.buildKeys();
			indices.add(lastIndex);
			serialize.writeIndexNode(lastIndex);
		}
		return indices;
	}
}
