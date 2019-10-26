package bpTree;

import java.util.*;

public class IndexNode extends Node{
	
	private int pageNum;
	private List<Node> children;
	private List<Integer> keys;
	
	public IndexNode(int pageNum) {
		this.pageNum =pageNum;
		children = new ArrayList<Node>();
		keys = new ArrayList<Integer>();
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getNumElement() {
		// TODO Auto-generated method stub
		return keys.size();
	}

	@Override
	public int getPage() {
		// TODO Auto-generated method stub
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
}
