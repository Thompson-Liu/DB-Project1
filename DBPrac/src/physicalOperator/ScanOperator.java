package physicalOperator;

import java.util.ArrayList;

import dataStructure.Catalog;
import dataStructure.DataTable;
import dataStructure.Tuple;
import fileIO.*;

/** the class for the scan operator that scans and reads input data tables. */
public class ScanOperator extends Operator {

	// Check if this will be inherited by the children class
	private BinaryTupleReader reader;
	private DataTable data;
	private int tupleCounter;
	private String tableName;  // if there is Alias then use Alias, otherwise use TableName
	private String dirName;   // only the name of the table,
						      // to get the directory and schema from the catalog

	/** @param tableName,hasAlias hasAlias is true if the tableName contains alias e.g. hasAlias if
	 * tableName= "Sailors AS S"
	 * @param hasAlias */
	public ScanOperator(String tableName, String aliasName) {
		this.tableName= tableName;
		this.dirName= tableName;
		Catalog catalog= Catalog.getInstance();
		if (aliasName != "") {
			dirName= tableName.replace("AS " + aliasName, "").trim();
			// if there's alias, always using alias name as the index for columns
			tableName= aliasName;
		}
		String dir= catalog.getDir(dirName);
		ArrayList<String> schema= catalog.getSchema(dirName);
		ArrayList<String> newSchema= (ArrayList<String>) schema.clone();
		for (int i= 0; i < schema.size(); i++ ) {
			newSchema.set(i, tableName + "." + schema.get(i));
		}
		data= new DataTable(tableName, newSchema);

		reader= new BinaryTupleReader(dir);
		for (Tuple t : reader.readData()) {
			data.addData(t);
		}
	}

	@Override
	public Tuple getNextTuple() {
		if (tupleCounter >= data.cardinality()) { return null; }
		return new Tuple(data.getRow(tupleCounter++ ));
	}

	
//	public DataTable getNextPage() {
//		
//	}

	/** reset read stream to re-read the data */
	@Override
	public void reset() {
		tupleCounter= 0;
	}

	@Override
	public void dump(TupleWriter writer) {
		writer.addTable(data.toArrayList());
		writer.dump();
		writer.close();
	}

	@Override
	public ArrayList<String> schema() {
		return data.getSchema();
	}

	@Override
	public String getTableName() {
		return data.getTableName();
	}

	@Override
	public DataTable getData() {
		return data;
	}
}
