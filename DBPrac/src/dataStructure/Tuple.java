package dataStructure;

import java.util.ArrayList;

public class Tuple {

	private ArrayList<Integer> dataTuple;

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
	
	public Tuple(ArrayList<Integer> tuple) {
		dataTuple = tuple;
	}
	
	public Tuple concateTuple(Tuple b) {
		ArrayList<Integer> result = (ArrayList<Integer>) dataTuple.clone();
		result.addAll(b.getTuple());
		return (new Tuple(result));
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
}
