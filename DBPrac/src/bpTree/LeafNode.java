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
		this.ridMap = new HashMap<Integer, ArrayList<int[]>> ();
	}
	

	@Override
	public int getType() {
		// TODO Auto-generated method stub
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
			this.ridMap.put(key,rids);
		}
	}
	
	

}
