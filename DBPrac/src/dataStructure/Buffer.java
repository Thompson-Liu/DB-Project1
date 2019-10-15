package dataStructure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Buffer {

	private ArrayList<Tuple> tuples;
	private int maxTuples;
	private ArrayList<String> sortOrder;
	
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
	
	public void setOrder(ArrayList<String> order) {
		sortOrder  = order;
	}
	
	/**
	 *  Sort a set of tuples by firstly primary order, then follows the sequence of schema
	 * @param dataTuples
	 * @param primary
	 * @param schema
	 */
	public void sortBuffer(ArrayList<Tuple> dataTuples,List<String> primary, ArrayList<String> schema) {
		// the new order of sorted data
		ArrayList<String> newOrder = new ArrayList<String>();
		for(String priorityCol : primary) {
			newOrder.add(priorityCol);
		}
		for(String col:schema) {
			if(!primary.contains(col)) {
				newOrder.add(col);
			}
		}
		Comparator<Tuple> myComparator= new Comparator<Tuple>() {
			@Override
			public int compare(Tuple tup1,Tuple tup2) {
				int result= 0;
				int ptr= 0;
				while (ptr < newOrder.size() && result == 0) {
					result= tup1.getTuple().get(schema.indexOf(newOrder.get(ptr))) -
							tup2.getTuple().get(schema.indexOf(newOrder.get(ptr)));
					ptr+= 1;
				}
				return result;
			}

		};
		tuples.sort(myComparator);
	}
	
	public void clear() {
		tuples = new ArrayList<Tuple>();
	}
}
