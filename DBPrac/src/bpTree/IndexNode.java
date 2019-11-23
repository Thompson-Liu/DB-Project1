package bpTree;

import java.util.ArrayList;
import java.util.List;

/** The class that represents an index node. */
public class IndexNode extends Node {

	private int pageNum;
	private ArrayList<Node> children;
	private List<Integer> keys;

	/** Constructs an index node. */
	public IndexNode(int pageNum) {
		this.pageNum= pageNum;
		children= new ArrayList<Node>();
		keys= new ArrayList<Integer>();
	}

	/** Get the list of keys.
	 * 
	 * @return a list of integers (keys) */
	public List<Integer> getKeys() {
		return keys;
	}

	@Override
	public int getType() {
		return 1;
	}

	@Override
	public int getNumElement() {
		return keys.size();
	}

	@Override
	public int getPage() {
		return pageNum;
	}

	/**
	 * Add children node
	 * 
	 * @param child
	 */
	public void addChild(Node child) {
		children.add(child);
	}

	@Override
	public int leastKey() {
		assert (children.size() != 0);

		return children.get(0).leastKey();
	}

	/** Build keys */
	public void buildKeys() {
		assert (children.size() != 0);

		for (int i= 1; i < children.size(); ++i) {
			keys.add(children.get(i).leastKey());
		}
	}

	@Override
	public ArrayList<Node> getChildren() {
		return children;
	}
}
