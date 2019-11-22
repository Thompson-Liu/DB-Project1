package dataStructure;

import java.util.ArrayList;

/**
 * data structure representing each row in the table
 */
public class Tuple {

	private ArrayList<Integer> dataTuple;

	/**
	 * The contructor that creates a tuple object and initializes the 
	 * Arraylist to store data
	 */
	public Tuple() {
		dataTuple = new ArrayList<Integer>();
	}
	
	/**
	 * Another constructor that will instantiate a tuple object from the data read 
	 * from the stream. Each line of data will be input as a line of string. The 
	 * data will be parsed and stored in an arraylist
	 * 
	 * @param dataInStr    the data stored as a string that will be parsed into an arraylist
	 */
	public Tuple(String dataInStr) {
		String strArr[]= dataInStr.trim().split("\\s*,\\s*");
		dataTuple= new ArrayList<Integer>(strArr.length);

		for (String x : strArr) {
			dataTuple.add(Integer.parseInt(x));
		}
	}
	
	/**
	 * Return a tuple obejct that represents the data stored in Arraylist
	 * 
	 * @param tuple    The data that will be converted into an arraylist
	 */
	public Tuple(ArrayList<Integer> tuple) {
		dataTuple = tuple;
	}
	
	/**
	 * Return a tuple after concating the argument with existing tuple
	 * @param b  tuple to be added 
	 * @param concatPos 
	 * @return  a new tuple with b added to current tuple
	 */
	public Tuple concateTuple(Tuple b, int concatPos) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < dataTuple.size(); ++i) {
			if (i == concatPos) {
				result.addAll(b.getTuple());
			}
			result.add(dataTuple.get(i));
		}
		return (new Tuple(result));
	}

	/**
	 * Get the arraylist representation of the data stored in tuple object
	 * @return     the data stored as an arraylist
	 */
	public ArrayList<Integer> getTuple() {
		return dataTuple;
	}
	
	/**
	 * Add data into the arraylist
	 * 
	 * @param data    the data to be added into the tuple
	 */
	public void addData(int data) {
		dataTuple.add(data);
	}

	/**
	 * Get the data stored at a specific index of the arraylist 
	 * 
	 * @param index    the index of the tuple that are searched for
	 * @return    the data located at a specific index
	 */
	public int getData(int index) {
		return dataTuple.get(index);
	}

	/**
	 * Print the data stored within the tuple in string representation
	 * 
	 * @return   the string that represents the data stored in tuple 
	 */
	public String printData() {
		String str = "";
		if (dataTuple == null) { return str; }
		for (int x : dataTuple) {
			str += (x + " ");
		}
		return str;
	}
	
	/**
	 * 
	 * Test to see if two tuples are the same 
	 * 
	 * @param tup2   the tuple to be compared with 
	 * @return   boolean that determines equality 
	 */
	public boolean equalTo(Tuple tup2) {
		return this.equals(tup2);
	}
}
