package physicalOperator;

import java.nio.channels.FileChannel;

import dataStructure.Tuple;

/** This class implements index scan, a file scan that only retrieves a range (subset) of tuples
 * from a relation file using B+-tree indices */
public class IndexScanOperator extends ScanOperator {

	private FileChannel fc;
	private int lo; // the low key, null means lack of a bound
	private int hi; // the high key, null means lack of a bound

	/** @param tableName: the relation to scan
	 * @param alias: the alias
	 * @param index: the index to use
	 * @param isClustered: whether the index is clustered or not
	 * @param lowkey: the lower bound of the range of the scan (inclusive)
	 * @param highkey: the higher bound of the range of the scan (inclusive) */
	public IndexScanOperator(String tableName, String alias, String index, boolean isClustered, int lowkey,
		int highkey) {
		super(tableName, alias);
		this.lo= lowkey;
		this.hi= highkey;

	}

	/** The initial root-to-leaf descent that gives the rid of the first matching tuple.
	 * 
	 * @param lowkey: the lower bound of the range of the scan (inclusive)
	 * @param highkey: the higher bound of the range of the scan (inclusive) */
	private void initialDescent() {

	}

	@Override
	public Tuple getNextTuple() {
		return null;
	}

}
