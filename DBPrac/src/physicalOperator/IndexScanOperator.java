package physicalOperator;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import bpTree.Deserializer;
import dataStructure.Catalog;
import dataStructure.Tuple;
import fileIO.BinaryTupleReader;

/** This class implements index scan, a file scan that only retrieves a range (subset) of tuples
 * from a relation file using B+-tree indices */
public class IndexScanOperator extends ScanOperator {

	private String tableName;
	private String colName;
	private ArrayList<String> schema;
	private FileInputStream fin;
	private FileChannel fc;
	private ByteBuffer buffer;
	private BinaryTupleReader reader;
	private boolean isClustered;
	private int lo; // the low key, null means lack of a bound
	private int hi; // the high key, null means lack of a bound
	private int[] startRid;
	private List<int[]> repo; // Rids
	private int ptr; // point to the correct position in repo

	/** @param tableName: the relation to scan
	 * @param alias: the alias
	 * @param index: the index column to use
	 * @param indexFile: the serialized B+- tree index file
	 * @param isClustered: whether the index is clustered or not
	 * @param lowkey: the lower bound of the range of the scan (inclusive)
	 * @param highkey: the higher bound of the range of the scan (inclusive)
	 * @throws IOException */
	public IndexScanOperator(String tableName, String alias, String index, String indexFile, boolean isClustered,
		int lowkey,
		int highkey) throws IOException {
		super(tableName, alias);
		fin= new FileInputStream(indexFile);
		fc= fin.getChannel();
		buffer= ByteBuffer.allocate(4096);
		this.tableName= tableName;
		this.colName= index;
		this.isClustered= isClustered;
		this.lo= lowkey;
		this.hi= highkey;
		Deserializer dsl= new Deserializer(indexFile);
		this.startRid= dsl.getRid(lo, hi);
		Catalog catalog= Catalog.getInstance();
		this.reader= new BinaryTupleReader(catalog.getDir(tableName)); // Dealing with alias?
		this.schema= catalog.getSchema(tableName); // Not sure
		this.ptr= 0;
		this.repo = new ArrayList<int []>();
		if (isClustered) {
			reader.reset(startRid[0], startRid[1]);
		} else {
			int startLeaf= dsl.getStartLeaf();
			while (true) {
				buffer.clear();
				fc.position(startLeaf * 4096);
				fc.read(buffer);
				assert (buffer.getInt(0) == 0);
				int numElements= buffer.getInt(4);
				int pos= 8;
				for (int i= 0; i < numElements; i++ ) {
					int key= buffer.getInt(pos);
					int numRids= buffer.getInt(pos + 4);
					if (key > hi) return;
					if (key >= lo && key <= hi) {
						for (int j= 0; j < numRids * 2; j+= 2) {
							int[] rid= new int[] { buffer.getInt(pos + 8 + j * 4),
									buffer.getInt(pos + 8 + (j + 1) * 4) };
							repo.add(rid);
						}
					}
					pos+= 8 + numRids * 8; // skip the first two metadata and numRids rids (each rid is 8 bytes)
				}
				startLeaf++ ;
			}
		}
	}

	@Override
	public Tuple getNextTuple() {
		if (isClustered) {
			Tuple tp= reader.readNextTuple(); // assumes it's reading sorted data file
			if (tp == null || tp.getData(schema.indexOf(colName)) > hi) return null;
			return tp;
		}
		if (ptr >= repo.size()) return null;
		reader.reset(repo.get(ptr)[0], repo.get(ptr)[1]);
		ptr+= 1;
		return reader.readNextTuple();

	}

	@Override
	public void reset() {
		if (isClustered) {
			reader.reset(startRid[0], startRid[1]);
		} else {
			ptr= 0;
		}

	}

	@Override
	public String getTableName() {
		return this.tableName;
	}

}