package dataStructure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fileIO.Logger;

public class Buffer {

	private ArrayList<Tuple> tuples;
	private int maxTuples;
	private ArrayList<String> newOrder;

	/**
	 * The constructor of Buffer 
	 * 
	 * @param numTuples   number of tuples allowed in a buffer
	 */
	public Buffer(int numTuples) {
		maxTuples = numTuples;
		tuples =  new ArrayList<Tuple>();
	}
	
	/**
	 * Return the tuples stores in buffer 
	 * @return the tuples stored
	 */
	public ArrayList<Tuple> getTuples(){
		return tuples;
	}

	/**
	 * Add another tuple into the buffer
	 * 
	 * @param tup  the new tuple added
	 */
	public void addData(Tuple tup) {
		tuples.add(tup);
	}

	/**
	 * Test to see if there are more spaces to store extra tuples
	 * 
	 * @return boolean that decides whether the buffer overflows
	 */
	public boolean overflow() {
		if (tuples.size() >= maxTuples) {
			return true;
		}
		return false;
	}

	/**
	 * Test to see if the buffer is empty
	 * 
	 * @return boolean to decide whether the buffer is empty 
	 */
	public boolean empty() {
		return tuples.size() == 0;
	}

	/**
	 * return the tuple at the index
	 * 
	 * @param index   index of the interested tuple
	 * @return   the tuple returned 
	 */
	public Tuple getTuple(int index) {
		if (index >= tuples.size()) {
			return null;
		}
		return tuples.get(index);
	}

	/**
	 * The order of the bufferr that will be used to sort
	 * 
	 * @param order   the sorting order
	 */
	public void setOrder(ArrayList<String> order) {
		newOrder  = order;
	}

	/**
	 *  Sort a set of tuples by firstly primary order, then follows the sequence of schema
	 * @param dataTuples
	 * @param primary
	 * @param schema
	 */
	public void sortBuffer(List<String> primary, ArrayList<String> schema) {
		// the new order of sorted data
		newOrder = new ArrayList<String>();
		if(primary==null) {
			newOrder=schema;
		}
		else {
			for(String priorityCol : primary) {
				newOrder.add(priorityCol);
			}
			for(String col:schema) {
				if(!primary.contains(col)) {
					newOrder.add(col);
				}
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
		Logger log= Logger.getInstance();

//		log.dumpTable(tuples);
	}

	/**
	 * Clear the buffer 
	 * 
	 */
	public void clear() {
		tuples = new ArrayList<Tuple>();
	}
}
