package physicalOperator;

import java.io.PrintStream;
import java.util.ArrayList;

import dataStructure.DataTable;
import dataStructure.Tuple;
import fileIO.BinaryTupleWriter;

/** the abstract class that all operator classes extend. */
public abstract class Operator {

	/** @return Returns the next tuple read from the data */
	Tuple getNextTuple() {
		return null;
	}

	/** reset read stream to re-read the data */
	void reset() {
		return;
	}

	/** @return the schema of the data table that is read by the operator */
	public ArrayList<String> schema() {
		return null;
	}

	/** @return the table name from where the operator reads the data */
	public String getTableName() {
		return null;
	}

	/** @return the data read by the operator in DataTable data structure */
	public DataTable getData() {
		return null;
	}

	public void dump(BinaryTupleWriter writer) {
		// TODO Auto-generated method stub
		
	}
}
