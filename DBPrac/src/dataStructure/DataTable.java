package dataStructure;

import java.util.ArrayList;
import java.util.HashMap;

public class DataTable {

	private String name;
	private ArrayList<ArrayList<Integer>> data;
	private ArrayList<String> schema;

	public DataTable(String tableName, ArrayList<String> schema) {
		name= tableName;
		this.schema = schema;
		data= new ArrayList<ArrayList<Integer>>();
	}

	public String getTableName() {
		return name;
	}
	
	public ArrayList<Integer> getData(int index) {
		return data.get(index);
	}
	
	public ArrayList<String> getSchema(){
		return schema;
	}

	public void addData(ArrayList<Integer> newData) {
		data.add(newData);
	}
	
	public void deleteLastData() {
		data.remove(data.size() - 1);
	}
	
	public int cardinality() {
		return data.size();
	}

	public void printTable(DataTable dt) {
		for (ArrayList<Integer> x: data) {
			for (int y: x) {
				System.out.print(y + " ");
			}
			System.out.println();
		}
	}

	public void printTableInfo(DataTable dt) {
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
