package dataStructure;

import java.util.ArrayList;
import java.util.Arrays;

public class Tuple {
	
	private ArrayList<Integer> dataTuple;
	
	public Tuple(String dataInStr) {
		String strArr[] = dataInStr.split(",");
		dataTuple = new ArrayList<Integer>(strArr.length);
		
		for (String x: strArr) {
			dataTuple.add(Integer.parseInt(x));
		}
	}
	
	public ArrayList<Integer> getData() {
		return dataTuple;
	}
}
