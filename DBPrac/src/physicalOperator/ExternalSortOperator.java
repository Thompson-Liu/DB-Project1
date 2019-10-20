/**
 * For pass0, we used buffer to sort each table,
 * 	   pass 1 .. end, we use priority to keep track of the first tuple to merge among different runs
 * 		and a hashmap of tupleToReader to keep track of the first tuple to merge and the bufferReader it comes from
 *	The number of tupleReaders represent the number of pages we have in buffer, in order to hold the tuples to sort
 */
package physicalOperator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import dataStructure.Buffer;
import dataStructure.DataTable;
import dataStructure.Tuple;
import dataStructure.TupleComparator;
import fileIO.BinaryTupleReader;
import fileIO.BinaryTupleWriter;
import fileIO.TupleReader;
import fileIO.TupleWriter;

/** the class for the sort operator that sorts the data another operator generates. */
public class ExternalSortOperator extends Operator {

	private PriorityQueue<Tuple> intermediateTable;    // keep track of current
	private Operator childOp;
	private TupleReader sortedReader; 			// keep the sorted result in a file, and access by this reader
	private int totalPass;  					// total number of passes needed
	private Buffer memoryBuffer;
	private int bufferSize;
	private int tuplesPage;        // number of tuples per page
	private String dataFile;
	private ArrayList<String> schema;
	private ArrayList<String> colList;
	private int runs;   			// number of files in each pass
	// private boolean useBinary = false; // format of intermediate result
	private int pass= 0;   // the current order of pass

	/** @param childOp childOp is the child operator, e.g. ProjectOperator or SelectOperator
	 * @param colList colList is the list of column names to sort data by */
	public ExternalSortOperator(Operator childOp, List<String> colList, int bufferSize, String tempDir) {
		this.childOp= childOp;
		this.bufferSize= bufferSize;
		this.schema= childOp.schema();
		this.colList= (ArrayList<String>) colList;
		this.runs= tuplesPage= (int) Math.floor(1.0 * (4096) / (4.0 * (schema.size())));
		memoryBuffer= new Buffer(tuplesPage);
		// pass0 and get the total number of files stored
		int totalFiles= initialRun();
		runs= totalFiles;
		// 这里需要double check一下👇
		// calculate the total number of passes needed
		Double div= Math.ceil(runs / bufferSize);
		totalPass= (int) Math.ceil(Math.log(div) / Math.log(1.0 * (bufferSize - 1)));
		for (int curPass= 1; curPass < totalPass; curPass++ ) {
			int nextRuns= ExternalSort(curPass, runs);
			runs= nextRuns;
		}
		sortedReader= new BinaryTupleReader("/tempdir/" + "externalIntermediate" + Integer.toString(totalPass) + "0");
	}

	// pass0
	private int initialRun() {
		this.runs= 0;
		Tuple cur;
		while ((cur= childOp.getNextTuple()) != null) {
			if (memoryBuffer.overflow()) {
				memoryBuffer.sortBuffer(colList, schema);
				TupleWriter tuplesWriter= new BinaryTupleWriter(
					"/tempDir/" + "externalIntermediate" + Integer.toString(pass) + Integer.toString(runs));
				tuplesWriter.write(memoryBuffer.getTuples());
				memoryBuffer.clear();
				this.runs++ ;
			}
			memoryBuffer.addData(cur);
		}
		// the number of runs in current pass
		return this.runs;
	}

	/** @param pathnum the i-th order of pass of this externalSort
	 * @runs runs the number of runs for this pass */
	private int ExternalSort(int passnum, int runs) {
		// the number of merges needed for this pass
		int mergenum= (int) Math.ceil(1.0 * runs / (this.bufferSize - 1));
		int startTable= 0;
		for (int i= 0; i < mergenum; i++ ) {
			int endTable= Math.min(startTable + bufferSize, runs);
			merge(startTable, endTable, i);
			startTable= endTable;
		}
		return mergenum;
	}

	/** merge one of the runs from current pass
	 * 
	 * @param runRuns the number of runs in this merge
	 * @param currentRun the order of runs for this merge (currentRun-th run)
	 * @param numMerge the order of merge in current pass */
	private void merge(int firstTable, int endTable, int numMerge) {
		List<TupleReader> readerList= new ArrayList<TupleReader>();
		HashMap<Tuple, TupleReader> tupleToReader= new HashMap<Tuple, TupleReader>();
		// read previous sorted result
		for (int i= firstTable; i <= endTable; i++ ) {
			BinaryTupleReader tupleRead= new BinaryTupleReader(
				"/tempDir/" + "externalIntermediate" + Integer.toString(pass - 1) + Integer.toString(i));
			readerList.add(tupleRead);
			// hashmap will not overwrite even for tuple with same value as long as the tuples are coming from
			// different tupleReaders, which is the property
			tupleToReader.put(tupleRead.readNextTuple(), tupleRead);
		}
		BinaryTupleWriter tupleWrite= new BinaryTupleWriter(
			"/tempDir/" + "externalIntermediate" + Integer.toString(pass) + Integer.toString(numMerge));
		intermediateTable= new PriorityQueue(new TupleComparator(this.colList, this.schema));
		Tuple next;
		Tuple curnext;
		TupleReader curReader;
		// pulling tuple-wise of the first of runs of previous sorted table
		while ((next= intermediateTable.poll()) != null) {
			tupleWrite.addNextTuple(next);
			curReader= tupleToReader.get(next);
			tupleToReader.remove(next);
			if ((curnext= curReader.readNextTuple()) != null) {
				intermediateTable.add(curnext);
				tupleToReader.put(curnext, curReader);
			}
			// if this run finish delete this table from tempdir
			else {
				String dfile= curReader.getFileInfo();
				File deleteFile= new File(dfile);
				if (!deleteFile.delete()) {
					System.out.println("didn't delete this file" + dfile);
				}
			}
		}
	}

	/** @return the next tuple in the sorted TupleReader */
	@Override
	public Tuple getNextTuple() {
		Tuple tup= sortedReader.readNextTuple();
		return tup;
	}

	/** @return the schema of the data table that is sorted by the operator */
	@Override
	public ArrayList<String> schema() {
		return schema;
	}

	// 1. 需要double check
	@Override
	public void reset() {
		sortedReader.reset();
	}

	public void resetIndex(int ind) {
		sortedReader.reset(ind, 4 * schema.size());
	}

	// 2. 看dump到哪里 write the [temp] directory file to dump dir [output]
	@Override
	public void dump(TupleWriter writer) {
		Tuple tup;
		while ((tup= sortedReader.readNextTuple()) != null) {
			writer.addNextTuple(tup);
		}
		writer.dump();
		writer.close();
	}	

	/** @return the name of the table being sorted */
	@Override
	public String getTableName() {
		return childOp.getTableName();
	}

}
