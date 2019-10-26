package bpTree;

import java.util.*;

public class IndexNode extends Node{
	
	private int pageNum;
	private ArrayList<Node> children;
	private List<Integer> keys;
	
	public IndexNode(int pageNum) {
		this.pageNum =pageNum;
		children = new ArrayList<Node>();
		keys = new ArrayList<Integer>();
	}

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
	
	public void addChild(Node child) {
		children.add(child);
	}
	
	public int leastKey() {
		assert(children.size() != 0);
		
		return children.get(0).leastKey();
	}
	
	public void buildKeys() {
		assert(children.size() != 0);

		for (int i = 1; i < children.size(); ++i) {
			keys.add(children.get(i).leastKey());
		}
	}

	@Override
	public ArrayList<Node> getChildren() {
		return children;
	}
}
