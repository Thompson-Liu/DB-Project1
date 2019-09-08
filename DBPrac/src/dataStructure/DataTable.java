package dataStructure;

import java.util.ArrayList;

public class DataTable {

	private String name;
	private ArrayList<ArrayList<Integer>> data;

	public DataTable(String tableName) {
		name= tableName;
		data= new ArrayList<ArrayList<Integer>>();

	}

	public String getTableName() {
		return name;
	}

	public void addData(ArrayList<Integer> newData) {
		data.add(newData);
	}

	public ArrayList<Integer> getData(int index) {
		return data.get(index);
	}

	public void printTable(DataTable dt) {

	}

	public void printTableInfo(DataTable dt) {

	}
}
