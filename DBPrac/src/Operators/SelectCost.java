package Operators;

import java.util.ArrayList;
import java.util.List;
import dataStructure.BlueBox;
import dataStructure.Catalog;

public class SelectCost {

	private Catalog catalog;

	/**
	 * 
	 * @param tableName
	 */
	public SelectCost() {
		catalog = Catalog.getInstance();
		
	}

	/**
	 *  ["full","",""] if using full scan   
	 *  ["index","S.A","clustered"] if using index        here S is alias name
	 * @param tableName      the name of table not alias
	 * @param attributes       the range of column scanned
	 * @return
	 */
	public String[] selectScan(String tableName, String alias,List<BlueBox> attributes) {
		ArrayList<String> schema = catalog.getSchema(tableName);
		int numTuples =catalog.getTupleNums(tableName);
		int numPages= (int) Math.ceil(numTuples*4.0*schema.size()/4096);
		Catalog catalog = Catalog.getInstance();
		
		// start with full scan
		String[] plan = {"full","",""};
		int optPageIO=numPages;
		int level=3;   // assumed in write-out
		int maxVal;
		int minVal;
		double r;
		
		for(BlueBox box: attributes) {
			List<String> eqCols = box.getAttr();
			Integer[] range = box.getBound();
			range[0] = (range[0]==null) ? Integer.MIN_VALUE : range[0];
			range[1] = (range[1]==null) ? Integer.MAX_VALUE : range[1];
 			for(String tabcol:eqCols) {
 				int totalPageIO;
 				String col=tabcol.split("\\.")[1];
 				// check if could use clustered scan
				if(catalog.getIsClustered(tableName, col)) {
					
					int orihigh = catalog.getColRange(tableName, col)[1];
					int orilow = catalog.getColRange(tableName, col)[0];
					maxVal = Math.min(orihigh,range[0]);
					minVal = Math.max(orilow, range[1]);
					r =1.0*(maxVal-minVal)/(orihigh-orilow);        //reduction factor
					totalPageIO= (int) (level+r*numPages);
					if(totalPageIO <optPageIO) {
						optPageIO = totalPageIO;
						plan = new String[] {"index",alias+"."+col,"clustered"};
					}
				}
				// check if use unclustered scan
				else if (catalog.getAllIndexes(tableName).contains(col) ){
					int numLeaves = catalog.getLeavesNum(tableName, col);
					int orihigh = catalog.getColRange(tableName, col)[1];
					int orilow = catalog.getColRange(tableName, col)[0];
					maxVal = Math.min(orihigh,range[0]);
					minVal = Math.max(orilow, range[1]);
					r = 1.0*(maxVal-minVal)/(orihigh-orilow);        //reduction factor
					totalPageIO = (int) (level + numLeaves*r + numTuples*r);
					if(totalPageIO <optPageIO) {
						optPageIO = totalPageIO;
						plan = new String[] {"index",alias + "." + col, "unclustered"};
					}
				}
			}
		}
		return plan;
	}
}
