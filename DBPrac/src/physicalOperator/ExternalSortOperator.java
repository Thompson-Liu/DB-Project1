package physicalOperator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dataStructure.DataTable;
import dataStructure.Tuple;
import fileIO.*;

/** the class for the sort operator that sorts the data another operator generates. */
public class ExternalSortOperator extends Operator {

	private DataTable fullData;
//	private ArrayList<DataTable> bufferTables;
	private TupleWriter writeInt;
	private int bufferSize;
	private String dataFile;        
	private ArrayList<String> schema;
	private int ptr;
	private boolean useBinary = false;// format of intermediate result
	private int pass = 0;   // the current order of pass

	/** @param childOp childOp is the child operator, e.g. ProjectOperator or SelectOperator
	 *  @param colList colList is the list of column names to sort data by */
	public ExternalSortOperator(Operator childOp, List<String> colList,int bufferSize) {
		ptr= -1;
		this.bufferSize=bufferSize;
		this.schema = childOp.schema();
		// the operator contains a small dataset that fit to main memory
		if(childOp.getFile()==null) {
			fullData = new DataTable(childOp.getTableName(), childOp.schema());
			fullData.setFullTable(childOp.getData().getFullTable());
			if (colList == null) {
				fullData.sortData(colList, schema);
			} else {
				fullData.sortData(colList, schema);
			}
		}
		else {
			
		}
		
	}
	
	
	private void process() {
		writeInt
	}

	/** @return the next tuple in the sorted buffer datatable */
	@Override
	public Tuple getNextTuple() {
		ptr+= 1;
		if (ptr < buffer.cardinality()) return new Tuple(buffer.getRow(ptr));
		return null;
	}

	/** @return the schema of the data table that is sorted by the operator */
	@Override
	public ArrayList<String> schema() {
		return buffer.getSchema();
	}

	@Override
	public void reset() {
		ptr= -1;
	}
	
	/**
	 *  dump the intermediate sorting result
	 * @param writer
	 */
	private void dumpIntermediate(TupleWriter writer) {
		if(useBinary) {
//			writer = ("tableName+pass")
		}
	}
	
	@Override
	public void dump(TupleWriter writer) {
		writer.writeTable(fullData.toArrayList());
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

	
	/**
	 *  Sort a set of tuples by firstly primary order, then follows the sequence of schema
	 * @param dataTuples
	 * @param primary
	 * @param schema
	 * @return
	 */
	public ArrayList<ArrayList<Integer>> sortData(ArrayList<ArrayList<Integer>> dataTuples,List<String> primary, ArrayList<String> schema) {
		// the new order of sorted data
		ArrayList<String> newOrder = new ArrayList<String>();
		for(String priorityCol : primary) {
			newOrder.add(priorityCol);
		}
		for(String col:schema) {
			if(!primary.contains(col)) {
				newOrder.add(col);
			}
		}
		Comparator<ArrayList<Integer>> myComparator= new Comparator<ArrayList<Integer>>() {
			@Override
			public int compare(ArrayList<Integer> arr1, ArrayList<Integer> arr2) {
				int result= 0;
				int ptr= 0;
				while (ptr < newOrder.size() && result == 0) {
					result= arr1.get(schema.indexOf(newOrder.get(ptr))) -
						arr2.get(schema.indexOf(newOrder.get(ptr)));
					ptr+= 1;
				}
				return result;
			}
		};
		dataTuples.sort(myComparator);
		return dataTuples;
	}
}
