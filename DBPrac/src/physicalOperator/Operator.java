package physicalOperator;

import java.util.ArrayList;
import dataStructure.Tuple;
import fileIO.*;

/** the abstract class that all operator classes extend. */
public abstract class Operator {

	/** @return Returns the next tuple read from the data */
	Tuple getNextTuple() {
		return null;
	}
	
	/**
	 * @return the name of the file containing the result of this operator
	 *         null if the dataset small and fit in to main memory
	 */
	public String getFile() {
		return null;
	}

	/** reset read streams to re-read the data */
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

	/**
	 * 
	 * Dump the resulted tuples using [writer]
	 * @param writer    The tuple Writer that will be used to dump the result 
	 */
	public void dump(TupleWriter writer) {
		// TODO Auto-generated method stub
		
	}
}
