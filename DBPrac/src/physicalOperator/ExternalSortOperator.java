package physicalOperator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.*;

import dataStructure.DataTable;
import dataStructure.Tuple;
import fileIO.*;

/** the class for the sort operator that sorts the data another operator generates. */
public class ExternalSortOperator extends Operator {

	private PriorityQueue<Tuple> intermediateTable;
	// 👇 need to be replaced
	private DataTable fullData;
	//	private ArrayList<DataTable> bufferTables;
	//	private TupleReader tuplesReader;
	private TupleWriter tuplesWriter;
	private int bufferSize;
	private int tuplesPage;        // number of tuples per page
	private String dataFile;    
	private ArrayList<String> schema;
	private ArrayList<String> sortCol;
	private int runs;   			// number of files in each pass
	private int ptr;
	private ArrayList<Tuple> curTable;
	private boolean useBinary = false;// format of intermediate result
	private int pass = 0;   // the current order of pass

	/** @param childOp childOp is the child operator, e.g. ProjectOperator or SelectOperator
	 *  @param colList colList is the list of column names to sort data by */
	public ExternalSortOperator(Operator childOp, List<String> colList,int bufferSize ,String tempDir) {
		ptr= -1;
		this.bufferSize=bufferSize;
		this.schema = childOp.schema();
		sortCol = (ArrayList<String>) colList;
		tuplesPage = (int) Math.floor(1.0*(4096)/(4.0*(schema.size())));
	
		initialRun(childOp);


		//		// the operator contains a small dataset that fit to main memory
		//		if(childOp.getFile()==null) {
		//			fullData = new DataTable(childOp.getTableName(), childOp.schema());
		//			fullData.setFullTable(childOp.getData().getFullTable());
		//			if (colList == null) {
		//				fullData.sortData(colList, schema);
		//			} else {
		//				fullData.sortData(colList, schema);
		//			}
		//		}
		//		else {
		//			ExternalSort(tempDir);
		//		}

	}

	private void initialRun(Operator childOp) {
		int fanin = bufferSize * tuplesPage;
		runs=0;
		int sofar = 0;
		Tuple cur;
		while ((cur=childOp.getNextTuple())!=null){
			if(sofar>=fanin) {
				tuplesWriter = new BinaryTupleWriter("/tempDir/"+Integer.toString(runs));
				tuplesWriter.write(curTable);
				curTable= new ArrayList<Tuple>();
				runs++;
				sofar=0;
			}
			curTable.add(cur);
			sofar++;
		}
		

	}

	private void ExternalSort(String tempDir) {
		int fanin = (bufferSize-1)*tuplesPage;
		for(int i=0;i<fanin;i++) {
			Tuple tup;
			tup = tuplesReader.readNextTuple();
			if(tup==null) {
				break;
			}else {
				intermediateTable.add(tup);
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
