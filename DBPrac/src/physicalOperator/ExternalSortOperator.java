/**
 * For pass0, we used buffer to sort each table,
 * 	   pass 1 .. end, we use priority to keep track of the first tuple to merge among different runs
 * 		and a hashmap of tupleToReader to keep track of the first tuple to merge and the bufferReader it comes from
 */
package physicalOperator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.*;
import dataStructure.*;
import fileIO.*;

/** the class for the sort operator that sorts the data another operator generates. */
public class ExternalSortOperator extends Operator {

	private PriorityQueue<Tuple> intermediateTable;    //  keep track of current 
	private Operator childOp;
	private HashMap<Tuple, TupleReader> tupleToReader;
	private TupleReader currentReader;
	private int totalPass;  					// total number of runs needed
	private Buffer memoryBuffer;
	private int bufferSize;
	private int tuplesPage;        // number of tuples per page
	private String dataFile;    
	private ArrayList<String> schema;
	private ArrayList<String> colList;
	private int runs ;   			// number of files in each pass
	private int ptr;
	private ArrayList<Tuple> curTable;
//	private boolean useBinary = false;			// format of intermediate result
	private int pass = 0;   // the current order of pass

	/** @param childOp childOp is the child operator, e.g. ProjectOperator or SelectOperator
	 *  @param colList colList is the list of column names to sort data by */
	public ExternalSortOperator(Operator childOp, List<String> colList,int bufferSize ,String tempDir) {
		ptr= -1;
		runs=0;
		this.childOp = childOp;
		this.bufferSize=bufferSize;
		this.schema = childOp.schema();
		this.colList =(ArrayList<String>) colList;
		
		tuplesPage = (int) Math.floor(1.0*(4096)/(4.0*(schema.size())));
		memoryBuffer = new Buffer(tuplesPage);
		// pass0
		initialRun();
		//这里需要算一下
		totalPass=0;
		// calculate the total number of runs needed
		currentReader = new BinaryTupleReader("/tempDir/"+"externalIntermediate"+Integer.toString(totalPass)+"1");
		ExternalSort();

	}

	// pass0
	private void initialRun() {
		this.runs=0;
		Tuple cur;
		while ((cur=childOp.getNextTuple())!=null){
			if (memoryBuffer.overflow()) {
				memoryBuffer.sortBuffer(colList, schema);
				TupleWriter tuplesWriter = new BinaryTupleWriter("/tempDir/"+"externalIntermediate"+Integer.toString(pass)+Integer.toString(runs));
				tuplesWriter.write(memoryBuffer.getTuples());
				memoryBuffer.clear();
				this.runs++;
			}
			memoryBuffer.addData(cur);
		}
	}


	private void ExternalSort() {
		// the new order of sorted data
		ArrayList<String> newOrder = new ArrayList<String>();
		// need to calculate the number of runs
		for(String priorityCol : colList) {
			newOrder.add(priorityCol);
		}
		for(String col:schema) {
			if(!colList.contains(col)) {
				newOrder.add(col);
			}
		}
		TupleReader minTupleReader;
		Tuple minTuple;
		while(runs>1) {
			int loops = (int) Math.ceil(1.0 * totalRuns / (bufferSize - 1));
			int lastRun = totalRuns % (bufferSize - 1);
			runs=0;

			for(int i=0;i<loops;i++) {
				int fanin = Math.min(runs,(bufferSize-1));
			}
		}

	}

	/**
	 *  merge one of the runs from current pass
	 * @param runs     the number of runs in this merge
	 * @param currentRun   the order of runs for this merge (currentRun-th run)
	 */
	private void merge(ArrayList<Integer> runs, int currentRun) {
		List<TupleReader> readerList = new ArrayList<TupleReader>();
		List<Tuple> tupleList= new ArrayList<Tuple>();
		
		// read previous sorted result
		for(int i : runs) {
			BinaryTupleReader tupleRead = new BinaryTupleReader("/tempDir/"+"externalIntermediate"+Integer.toString(pass-1)+Integer.toString(i));
			readerList.add(tupleRead) ;
			tupleList.add(tupleRead.readNextTuple());
		}
		
		intermediateTable = new PriorityQueue(new TupleComparator(this.colList,this.schema));
		for(int i=0;i< runs-1; i++) {
			if(tup==null) {
				break;
			}else {
				intermediateTable.add(tup);
			}
		}

		for (int i = 0; i < fanin - 2; ++i) {
			int result = compare(tupleList.get(i), tupleList.get(i + 1));
			reusl
		}
	}
}





/** @return the next tuple in the sorted buffer datatable */
@Override
public Tuple getNextTuple() {
	ptr+= 1;
	Tuple tup=tuplesReader.readNextTuple();
	//		if (ptr < buffer.cardinality()) return new Tuple(buffer.getRow(ptr));
	return tup;
}

/** @return the schema of the data table that is sorted by the operator */
@Override
public ArrayList<String> schema() {
	return schema;
}

//需要想一下怎么implement
@Override
public void reset() {

	ptr= -1;
}

/**
 *  dump the intermediate sorting result
 * @param writer
 */
private void dumpIntermediate(TupleWriter writer, String tempDir) {
	if(useBinary) {
		//			writer = (tempDir+"tableName+pass")
	}
}

@Override
public void dump(TupleWriter writer) {
	writer.addTable(fullData.toArrayList());
	writer.dump();
	writer.close();
}

/** @return the datable after sorting */
@Override
public DataTable getData() {
	return buffer;
}

/** @return the name of the table being sorted */
@Override
public String getTableName() {
	return buffer.getTableName();
}

}
