package dataStructure;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DataTable {

	private String name;
	private ArrayList<ArrayList<Integer>> data;
	private ArrayList<String> schema;

	/** The constructor of a DataTable object
	 * 
	 * @param tableName the name used for the table
	 * @param schema the schema of the table */
	public DataTable(String tableName, ArrayList<String> schema) {
		name= tableName;
		data= new ArrayList<ArrayList<Integer>>();
		this.schema= schema;
	}

	/** Get the name of the data table
	 * 
	 * @return the name of the table */
	public String getTableName() {
		return name;
	}

	/** get all the data in the datatable as a 2D arrayList
	 * 
	 * @return the full data Table's data */
	public ArrayList<ArrayList<Integer>> getFullTable() {
		return ((ArrayList<ArrayList<Integer>>) data.clone());
	}

	/** Change the datatable's data into another 2D arrayList given by the input
	 * 
	 * @param d set the full datatable's data to d */
	public void setFullTable(ArrayList<ArrayList<Integer>> d) {
		this.data= d;
	}

	/** Get the table's schema
	 * 
	 * @return the schema of the table */
	public ArrayList<String> getSchema() {
		return schema;
	}

	/** Set the schema of the datatable
	 * 
	 * @param changeSchema set the schema of the data table into another arrayList of strings
	 * [changeSchema] */
	public void setSchema(ArrayList<String> changeSchema) {
		this.schema= changeSchema;
	}

	/** Add a line of data into the data table
	 * 
	 * @param newData new data to insert into the data table */
	public void addData(ArrayList<Integer> newData) {
		data.add(newData);
	}

	/** Add a tuple of data into the data table
	 * 
	 * @param tup A tuple that will be inserted into the data table */
	public void addData(Tuple tup) {
		data.add(tup.getTuple());
	}

	/** Get the number of elements stored in the datatable
	 * 
	 * @return the number of elements stored in the datatable */
	public int cardinality() {
		return data.size();
	}

	/** Get only one column of data from the data table
	 * 
	 * @param columnName the name of the column
	 * @return the data only under one column of the datatable */
	public ArrayList<Integer> getData(String columnName) {
		return data.get(schema.indexOf(columnName));
	}

	/** Get a row of data from the data table
	 * 
	 * @param r the index of the row
	 * @return A row of data stored at the index r of the datatable */
	public ArrayList<Integer> getRow(int r) {
		return data.get(r);
	}

	/** Sort the data in the data table in place
	 * 
	 * @param colList: A list of columns to order by, sortData will first sort data by the first element
	 * in colList, if there is a tie, it will then sort data by the second element, etc.
	 * @param colSchema: the schema of the current data table */
	public void sortData(List<String> colList, ArrayList<String> colSchema) {
		Comparator<ArrayList<Integer>> myComparator= new Comparator<ArrayList<Integer>>() {
			@Override
			public int compare(ArrayList<Integer> arr1, ArrayList<Integer> arr2) {
				int result= 0;
				int ptr= 0;
				while (ptr < colList.size() && result == 0) {
					result= arr1.get(colSchema.indexOf(colList.get(ptr))) -
						arr2.get(colSchema.indexOf(colList.get(ptr)));
					ptr+= 1;
				}
				return result;
			}
		};
		data.sort(myComparator);
	}

	/** Print the data stored in the datatable, seperated by coma to the printstream
	 * 
	 * @param ps The print strema that the data will be printed to */
	public void printTable(PrintStream ps) {
		for (ArrayList<Integer> x : data) {
			for (int i= 0; i < x.size() - 1; ++i) {
				ps.print(x.get(i) + ",");
			}
			ps.print(x.get(x.size() - 1));
			ps.println();
		}
	}

	/** Print the table information, which are the name of the able, the directory of the table, and the
	 * schema of the table. 
	 * */
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
