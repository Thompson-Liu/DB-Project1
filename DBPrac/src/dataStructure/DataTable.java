package dataStructure;

import java.util.ArrayList;

public class DataTable {

	private String name;
	private ArrayList<ArrayList<Integer>> data;
	private ArrayList<String> schema;

	public DataTable(String tableName, ArrayList<String> schema) {
		name= tableName;
		data= new ArrayList<ArrayList<Integer>>();
	}

	public String getTableName() {
		return name;
	}
	
	public ArrayList<String> getSchema() {
		return schema;
	}

	public void addData(ArrayList<Integer> newData) {
		data.add(newData);
	}

	public int cardinality() {
		return data.size();
	}

	public ArrayList<Integer> getData(String columnName) {
		return data.get(schema.indexOf(columnName));
	}

	public void sortData(String colName) {
		int colIndex= schema.indexOf(colName);
		data.sort((l1, l2) -> l1.get(colIndex).compareTo(l2.get(colIndex)));
	}

	public void printTable() {
		for (ArrayList<Integer> x : data) {
			for (int y : x) {
				System.out.print(y + " ");
			}
			System.out.println();
		}
	}

	public void printTableInfo() {
		System.out.println("Table name: " + name);

		Catalog catalog= Catalog.getInstance();
		System.out.println("Table directory: " + catalog.getDir(name));

		System.out.print("Table schema is: [");
		for (String columnName : schema) {
			System.out.print(columnName + " ");
		}
		System.out.print("]\n");
	}
}
