package dataStructure;

import java.util.ArrayList;

public class Tuple {

	// tuple value
	private ArrayList<Integer> dataTuple;
	
	// schema of the tuple on the table
//	private ArrayList<String> schema;
	// the table this tuple belongs to
//	private String table;

	public Tuple() {
		dataTuple = new ArrayList<Integer>();
	}
	
	public Tuple(String dataInStr) {
		String strArr[]= dataInStr.split(",");
		dataTuple= new ArrayList<Integer>(strArr.length);

		for (String x : strArr) {
			dataTuple.add(Integer.parseInt(x));
		}
	}
	
	// constructor directly take in the value of tuple
	public Tuple(ArrayList<Integer> value) {
		dataTuple = value;
	}

	public ArrayList<Integer> getTuple() {
		return dataTuple;
	}
	
	public void addData(int data) {
		dataTuple.add(data);
	}

	public int getData(int index) {
		return dataTuple.get(index);
	}

	public String printData() {
		String str = "";
		if (dataTuple == null) { return str; }
		for (int x : dataTuple) {
			str += (x + " ");
		}
		return str;
	}
	
	public Tuple concat(Tuple a, Tuple b) {
		a.dataTuple.addAll(b.dataTuple);
		return a;
	}
}
