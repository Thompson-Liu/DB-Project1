package operator;

import dataStructure.Tuple;
import dataStructure.DataTable;

public abstract class Operator {

	Tuple getNextTuple(){
		return null;
	}
	
	void reset() {
		return;
	}
	
	public DataTable dump() {
		return null;
	}
}
