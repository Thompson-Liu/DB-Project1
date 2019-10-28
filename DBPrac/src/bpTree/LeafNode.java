package bpTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LeafNode extends Node {

	private List<Integer> keys;
	private HashMap<Integer, ArrayList<int[]>> ridMap;
	private int pageNum;
	
	public LeafNode(int pageNum) {
		this.pageNum = pageNum;
		ridMap = new HashMap<Integer, ArrayList<int[]>> ();
		keys = new ArrayList<Integer>();
		
	}
	

	@Override
	public int getType() {
		return 0;
	}

	@Override
	public int getNumElement() {
		return keys.size();
	}


	@Override
	public int getPage() {
		return pageNum;
	}
	
	public void add(int key, int[] rid) {
		if(this.keys.contains(key)) {
			this.ridMap.get(key).add(rid);
		}
		else {
			this.keys.add(key);
			ArrayList<int[]> rids = new ArrayList<int[]>();
			rids.add(rid);
			this.ridMap.put(key,rids);
		}
	}
	
	public int getKey(int index) {
		return keys.get(index);
	}
	
	public ArrayList<int[]> getRids(int key) {
		return ridMap.get(key);
	}
	
	public void addDatas(int key, ArrayList<int[]> rids) {
		assert(!keys.contains(key));
		
		keys.add(key);
		ridMap.put(key, rids);
	}


	@Override
	public int leastKey() {
		return keys.get(0);
	}


	@Override
	public ArrayList<Node> getChildren() {
		return null;
	}
}
