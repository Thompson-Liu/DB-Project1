package bpTree;

import java.util.*;

public class IndexNode extends Node{
	
	private int pageNum;
	private List<Node> children;
	private List<Integer> keys;
	
	public IndexNode(int pageNum) {
		this.pageNum =pageNum;
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
		
	}

}
