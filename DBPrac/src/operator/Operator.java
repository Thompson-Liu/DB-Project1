package operator;

import java.io.PrintStream;
import java.util.ArrayList;

import dataStructure.DataTable;
import dataStructure.Tuple;

public abstract class Operator {

	Tuple getNextTuple() {
		return null;
	}

	void reset() {
		return;
	}

	public void dump(PrintStream ps) {
		return;
	}

	public ArrayList<String> schema() {
		return null;
	}

	public String getTableName() {
		return null;
	}

	public DataTable getData() {
		return null;
	}
}
