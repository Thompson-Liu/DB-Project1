package dataStructure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class DataTable {
	
	private String name;
	private ArrayList<ArrayList<Integer>> data;
	private A
	
	public DataTable(String tableName) {
		name = tableName;
		data = new ArrayList<ArrayList<Integer>>();
		
	}
	
	public String getTableName() {
		return name;
	}
		
	public void addData(ArrayList<Integer> newData) {
		data.add(newData);
	}
	
	public ArrayList<Integer> getData(int index){
		return data.get(index);
	}
	
	public void printTable(DataTable dt) {
		
	}
	
	public void printTableInfo(DataTable dt) {
		
	}
}
