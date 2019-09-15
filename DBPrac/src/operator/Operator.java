package operator;

import dataStructure.Tuple;
import dataStructure.DataTable;

public abstract class Operator {

	Tuple getNextTuple(String tableName){
		return null;
	}
	
	void reset() {
		return;
	}
	
	public DataTable dump(String tableName) {
		return null;
	}
}
