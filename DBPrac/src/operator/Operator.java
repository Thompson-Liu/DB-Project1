package operator;

import dataStructure.Tuple;
import java.io.PrintStream;
import java.util.ArrayList;

import dataStructure.DataTable;
public abstract class Operator {

	Tuple getNextTuple(){
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
}
