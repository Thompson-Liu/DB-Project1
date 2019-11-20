package physicalOperator;

import java.io.IOException;
import java.util.ArrayList;

import dataStructure.Catalog;
import dataStructure.Tuple;
import fileIO.BinaryTupleReader;
import fileIO.TupleWriter;
import utils.PhysicalPlanWriter;

/** the class for the scan operator that scans and reads input data tables. */
public class ScanOperator extends Operator {

	// Check if this will be inherited by the children class
	private BinaryTupleReader reader;
	private ArrayList<String> schema;
	private String tableName;  // if there is Alias then use Alias, otherwise use TableName
	private String dirName;   // only the name of the table,
						      // to get the directory and schema from the catalog

	/** @param tableName,hasAlias hasAlias is true if the tableName contains alias e.g. hasAlias if
	 * tableName= "Sailors AS S"
	 * @param hasAlias */
	public ScanOperator(String tableName, String aliasName) {
		this.tableName= (aliasName != "") ? aliasName : tableName;
		this.dirName= tableName;

		Catalog catalog= Catalog.getInstance();
		String dir= catalog.getDir(dirName);
		ArrayList<String> schema= catalog.getSchema(dirName);
		ArrayList<String> newSchema= (ArrayList<String>) schema.clone();
		for (int i= 0; i < schema.size(); i++ ) {
			newSchema.set(i, this.tableName + "." + schema.get(i));
		}
		this.schema= newSchema;

		reader= new BinaryTupleReader(dir);
	}

	@Override
	public Tuple getNextTuple() {
		Tuple t= reader.readNextTuple();
		return t;
	}

	/** reset read stream to re-read the data */
	@Override
	public void reset() {
		reader.reset();
	}

	@Override
	public void dump(TupleWriter writer) {
		Tuple t;
		while ((t= getNextTuple()) != null) {
			writer.addNextTuple(t);
		}
		writer.dump();
		writer.close();
	}

	@Override
	public ArrayList<String> schema() {
		return this.schema;
	}

	@Override
	public String getTableName() {
		return this.tableName;
	}

	@Override
	public void accept(PhysicalPlanWriter ppw) {
		try {
			ppw.visit(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
