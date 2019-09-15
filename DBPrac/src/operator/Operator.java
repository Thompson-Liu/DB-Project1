package operator;

import dataStructure.Tuple;
import dataStructure.DataTable;

public abstract class Operator {

	Tuple getNextTuple(DataTable table){
		return null;
	}
	
	void reset() {
		return;
	}
	
	public DataTable dump(DataTable table) {
		return null;
	}
}
