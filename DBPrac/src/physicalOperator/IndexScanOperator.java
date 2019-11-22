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
import utils.PhysicalPlanWriter;

/** This class implements index scan, a file scan that only retrieves a range (subset) of tuples
 * from a relation file using B+-tree indices */
public class IndexScanOperator extends ScanOperator {

	private String oriTableName;
	private String colName;
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
		oriTableName = tableName;
		fin= new FileInputStream(indexFile);
		fc= fin.getChannel();
		buffer= ByteBuffer.allocate(4096);
		this.colName= index;
		this.isClustered= isClustered;
		this.lo= lowkey;
		this.hi= highkey;
		Deserializer dsl= new Deserializer(indexFile);
		this.startRid= dsl.getRid(lo, hi);
		Catalog catalog= Catalog.getInstance();
		catalog.setLeavesNum(tableName, index, dsl.getNumLeaves());
		this.reader= new BinaryTupleReader(catalog.getDir(tableName)); // Dealing with alias?

		this.ptr= 0;
		this.repo= new ArrayList<int[]>();
		if (isClustered) {
			reader.reset(startRid[0], startRid[1]);
		} else {
			int startLeaf= dsl.getStartLeaf();
			while (true) {

				buffer.clear();
				fc.position(startLeaf * 4096);
				fc.read(buffer);
				if (buffer.getInt(0) != 0) return;
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
			if (tp == null || tp.getData(super.schema().indexOf(colName)) > hi) return null;
			return tp;
		}
		if (ptr >= repo.size()) return null;
		reader.reset(repo.get(ptr)[0], repo.get(ptr)[1]);
		ptr+= 1;
		Tuple temp =reader.readNextTuple();
		System.out.println(this.hi);
		System.out.println(this.lo);
		System.out.println(this.oriTableName);
		System.out.println(temp.getTuple().toString());
		return temp;
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
		return super.getTableName();
	}
	
	public String getOriginalTableName() {
		return oriTableName;
	}

	public String getCol() {
		return colName;
	}

	public int getLow() {
		return lo;
	}

	public int getHigh() {
		return hi;
	}

	@Override
	public void accept(PhysicalPlanWriter ppw) {
		try {
			ppw.visit(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}