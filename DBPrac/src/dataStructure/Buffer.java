package dataStructure;

import java.util.ArrayList;

public class Buffer {

	private ArrayList<Tuple> tuples;
	private int maxTuples;
	
	public Buffer(int numTuples) {
		maxTuples = numTuples;
		tuples =  new ArrayList<Tuple>();
	}
	
	public void addData(Tuple tup) {
		tuples.add(tup);
	}
	
	public boolean overflow() {
		if (tuples.size() >= maxTuples) {
			return true;
		}
		return false;
	}
	
	public boolean empty() {
		return tuples.size() == 0;
	}
	
	public Tuple getTuple(int index) {
		if (index >= tuples.size()) {
			return null;
		}
		return tuples.get(index);
	}
	
	public void clear() {
		tuples = new ArrayList<Tuple>();
	}
}
