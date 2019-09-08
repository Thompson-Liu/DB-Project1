package dataStructure;

import java.util.ArrayList;

public class Tuple {

	private ArrayList<Integer> dataTuple;

	public Tuple(String dataInStr) {
		String strArr[]= dataInStr.split(",");
		dataTuple= new ArrayList<Integer>(strArr.length);

		for (String x : strArr) {
			dataTuple.add(Integer.parseInt(x));
		}
	}

	public ArrayList<Integer> getTuple() {
		return dataTuple;
	}

	public int getData(int index) {
		return dataTuple.get(index);
	}

	public String printData() {
		String str= "";
		for (int x : dataTuple) {
			str+= x + " ";
		}
		return str;
	}
}
