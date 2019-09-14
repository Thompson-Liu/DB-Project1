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
	
	public int cardinality() {
		return data.size();
	}
	
	public void deleteLastData() {
		data.remove(data.size() - 1);
	}

	public ArrayList<Integer> getData(int index) {
		return data.get(index);
	}

	public void printTable() {
		for (ArrayList<Integer> x: data) {
			for (int y: x) {
				System.out.print(y + " ");
			}
			System.out.println();
		}
	}

	public void printTableInfo() {
		System.out.println("Table name: " + name);
		
		Catalog catalog = Catalog.getInstance();
		System.out.println("Table directory: " + catalog.getDir(name));
		
		System.out.print("Table schema is: [");
		for (String columnName: catalog.getSchema(name)) {
			System.out.print(columnName + " ");
		}
		System.out.print("]\n");
	}
}
