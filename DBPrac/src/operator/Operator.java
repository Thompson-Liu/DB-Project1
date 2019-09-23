package operator;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import dataStructure.DataTable;
import dataStructure.Tuple;

public abstract class Operator {

	/**
	 * @return Returns the next tuple read from the data
	 */
	Tuple getNextTuple() {
		return null;
	}

	/**
	 * reset read stream to re-read the data
	 */
	void reset() {
		return;
	}

	/**
	 * Prints the data read by operator to the PrintStream [ps]
	 * 
	 * @param ps      The print stream that the output will be printed to
	 * @param print   boolean decides whether the data will actually be printed 
	 */
	public void dump(PrintStream ps, boolean print) {
		return;
	}

	/**
	 * @return the schema of the data table that is read by the operator
	 */
	public ArrayList<String> schema() {
		return null;
	}

	/**
	 * @return the table name from where the operator reads the data
	 */
	public String getTableName() {
		return null;
	}

	/** 
	 * @return the data read by the operator in DataTable data structure
	 */
	public DataTable getData() {
		return null;
	}
}
