/**
 * Calculate the join cost of different join orders and get the optimal Operator of the join tree
 */
package Operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import dataStructure.BlueBox;
import dataStructure.Catalog;
import dataStructure.UnionFind;
import logicalOperators.Leaf;
import logicalOperators.LogicalOperator;
import logicalOperators.SelectLogOp;
import net.sf.jsqlparser.expression.Expression;


public class JoinOptimizer {
	private ArrayList<String> aliasNames;                
	private List<LogicalOperator> baseOperators;     // one-to-one position corresponding with tableName above
	private Catalog cat;
	private UnionFind unionFind;
	private HashMap<HashSet<String> , PlanInfo> subsetPlan;        // store the optimal subset and corresponding information

	/**
	 * 
	 * @param tableNames           注！！ tableNames NOT Alias
	 * @param joinChildren
	 * @param joinExpr  
	 */
	public JoinOptimizer(List<LogicalOperator> joinChildren, UnionFind unionFind) {
		this.unionFind=unionFind;
		subsetPlan= new HashMap<HashSet<String>, PlanInfo>();
		cat=Catalog.getInstance(); 
		this.aliasNames = new ArrayList<String>();
		this.baseOperators  = joinChildren;
		
		//start from base case bottom up until the the construction contains whole
		for(int subPlanSize=1;subPlanSize<=baseOperators.size();subPlanSize++){
			// each table could be the top most table in the subPlan
			for (int topMost= 0; topMost < baseOperators.size(); topMost++ ) {
				//base case
				if(subPlanSize==1) {
					LogicalOperator cur = this.baseOperators.get(topMost);
					if(cur instanceof SelectLogOp) {
						selectionVupdate( (SelectLogOp)cur);
					}else {
						baseTableVupdate((Leaf)cur);
					}
				}else {
					ArrayList<String> prevSub=new ArrayList<String>();  // track the previous traversed subPlans
					traverseSubPlans(topMost,subPlanSize,0,prevSub);
				}
			}
		}
		
	}

	/**|
	 *  A B C D
	 *  {A}  {B}  {C} {D}
	 *  {AB} {BA} {BC} ..
	 *  {BC A}  { CD A} {BD A} {AC B}
	 *  {ABC D}  {BCD A}  {ACD B} { ABD C}
	 */
	/**  Compute cost of join in a bottom-up fashion using Dynamic Programming. */
	public ArrayList<LogicalOperator> findOptimalJoinOrder() {
		HashSet<String> prevSubTables = new HashSet<String>(this.aliasNames);
		PlanInfo optimalPlan = this.subsetPlan.get(prevSubTables);
		ArrayList<LogicalOperator> ret = optimalPlan.getOpsCopy();
		System.out.println(optimalPlan.getAliasNames().toString());
		return optimalPlan.getOpsCopy();
	}

	/**
	 *  get all the possible combination in (targetSize), using topMost as the right most table	
	 *  update the subset plan of {T1, ...T_(size-1),topMost} tables with the optimal operator joining tree	
	 * @param topMost        index of right most table to start
	 * @param targetSize     total size of this subPlan
	 * @param curLen         lenghth of the cumulated tables
	 * @param curPos         pos of the next table to be added
	 * @param prevSubPlan    array of tables
	 * A B CDE    B    { 3 ACD   3 ACE    3CDE   }  
	 * ABCD    B  targetsize=3  {AC}  {AD}  {CD}
	 */
	public void traverseSubPlans(int topMost,int targetSize, int curPos,ArrayList<String> prevSubPlan) {
		// get one set of size=subPlanSize, compute cost of joining topMost with the best plan of this set
		if(prevSubPlan.size()==targetSize-1) {
			ArrayList<String> prevSubCopy = new ArrayList<String>();
			prevSubCopy.addAll(prevSubPlan);
			HashSet<String> prevSubTables = new HashSet<String>(prevSubCopy);
			PlanInfo prevOptPlan = this.subsetPlan.get(prevSubTables);
			HashSet<String> topMostTable = new HashSet<String>();
			topMostTable.add(this.aliasNames.get(topMost));
			PlanInfo topMostPlan = this.subsetPlan.get(topMostTable);
			PlanInfo newPlan = joinVupdate(prevOptPlan,topMostPlan);
			prevSubTables.add(this.aliasNames.get(topMost));
			//update the hashMap if this plan is the optimal cost among operators with same set of tables 
			//or it is the first time to process this set of tables
			Boolean condition = !subsetPlan.containsKey(prevSubTables) || subsetPlan.get(prevSubTables).getCost()>newPlan.getCost();
			if(condition) {
				this.subsetPlan.put(prevSubTables, newPlan);
			}
			return;
		}
		// Terminate, if no more table to join or current table size in plan exceeds
		else if (curPos>=this.baseOperators.size() || prevSubPlan.size()>=targetSize) {
			return;
		}
		// Skip if this new table is the topMost table
		else if (curPos == topMost ) {
			int newPos = curPos+1;
			traverseSubPlans(topMost,targetSize,newPos,prevSubPlan);
			return;
		}
		// Recursive: for each curPos, 2 cases: either select it / not select it
		else {
			int newPos=curPos+1;
			ArrayList<String> addedSubPlan = new ArrayList<String>();
			addedSubPlan.addAll(prevSubPlan);
			addedSubPlan.add(this.aliasNames.get(curPos));
			traverseSubPlans(topMost,targetSize,newPos,addedSubPlan);

			traverseSubPlans(topMost,targetSize,newPos,prevSubPlan);
		}
	}


	/** helper method to set the V of baseTable and the total number of tuple
	 * @param Operator    either scan or indexScan on base(Table) 
	 */
	private void baseTableVupdate(Leaf baseOp) {
		// access catalog
		String tableName =baseOp.getTableName();
		String aliasName = baseOp.getAlias();
		if(aliasName=="") {
			aliasName=tableName;
		}
		this.aliasNames.add(aliasName);
		int numTuples = cat.getTupleNums(tableName);

		// set up new PlanInfo
		ArrayList<LogicalOperator> AbaseOp = new ArrayList<LogicalOperator>();
		AbaseOp.add(baseOp);
		PlanInfo basePlan = new PlanInfo(AbaseOp);
		basePlan.addAliasName(aliasName);
		ArrayList<String> schema = cat.getSchema(tableName);
		for(String col:schema) {
			//V(R,A)=max- min +1
			int v = cat.getColRange(tableName, col)[1]-cat.getColRange(tableName, col)[0]+1;
			basePlan.addColV(aliasName, col, v);
		}
		basePlan.setCost(0);
		basePlan.setTotalTuples(numTuples);
		HashSet<String> base =new HashSet<String>();
		base.add(aliasName);
		this.subsetPlan.put(base,basePlan);
	}

	/** helper method to set the V of selection table
	 * @param selectOp
	 */
	private void selectionVupdate(SelectLogOp selectOp) {
		Catalog catalog = Catalog.getInstance();
		String tableName = selectOp.getTableName();
		ArrayList<String> schema = catalog.getSchema(tableName);
		int numTuples = catalog.getTupleNums(tableName);
		ArrayList<LogicalOperator> AselectOp = new ArrayList<LogicalOperator>();
		AselectOp.add(selectOp);
		String aliasName = selectOp.getAlias();
		if(aliasName=="") {
			aliasName=tableName;
		}
		this.aliasNames.add(aliasName);

		// setup the PlanInfo of this select operator
		PlanInfo curPlan = new PlanInfo(AselectOp);
		curPlan.addAliasName(aliasName);

		// TODO (call UnionFind function,e.g. getTableColRange(TableName) to get)
		ArrayList<BlueBox> blueBoxes = this.unionFind.findSelect(aliasName);
		double reductionFactor =1;
		for(String col:schema) {
			int oriHigh = catalog.getColRange(tableName, col)[1];
			int oriLow=catalog.getColRange(tableName, col)[0];
			for (BlueBox box: blueBoxes) {
				List<String> colRange = box.getAttr();
				if(colRange.contains(col)) {  // check if col is in expr {R.A=3 or R.A<4} with low and high bound
					int newHigh = (box.getUpper()==null) ? Integer.MAX_VALUE : box.getUpper();
					int newLow = (box.getLower()==null) ? Integer.MIN_VALUE : box.getLower();
					newHigh = Math.min(newHigh,oriHigh);     
					newLow = Math.max(newLow, oriLow);
					int curReductionF = (int)(newHigh-newLow)/(oriHigh-oriLow);
					reductionFactor = reductionFactor*curReductionF;
					int v = newHigh-newLow+1;
					curPlan.addColV(aliasName, col, v);
				}
				else {
					int v = oriHigh-oriLow+1;
					curPlan.addColV(aliasName, col, v);
				}
			}
		}
		int newNum = (int) (reductionFactor*numTuples);
		newNum = (newNum==0) ?  1:newNum;      // total number of tuples round to 1 if zero
		curPlan.setCost(0);
		curPlan.setTotalTuples(newNum);
		// update the V(R,A) , if there is less tuples than the V
		for(String col:schema) {
			if(curPlan.getColV(tableName, col)==null || curPlan.getColV(aliasName, col)>newNum) {
				curPlan.addColV(aliasName, col, newNum);
			}
		}
		HashSet<String> curTable = new HashSet<String>();
		curTable.add(aliasName);
		this.subsetPlan.put(curTable,curPlan);
	}

	private PlanInfo joinVupdate(PlanInfo prevOptPlan,PlanInfo topMostPlan) {
		List<ArrayList<String>> equTableColSet = new ArrayList<ArrayList<String>>();
		if(this.unionFind!=null) {
			equTableColSet= this.unionFind.findJoin(prevOptPlan.getAliasNames(),topMostPlan.getAliasNames().get(0));  
		}
		//👆 Alias+col : equalities among a list of tables since prevOptPlan contains multiple tables
		//  e.g.   " R.a=R.h=B.C  AND  S.d=B.e "    prevTables:[R,S]  topMostTable: [B]
		//          WANT:  [ ["R.a","R.h" , "B.c"], ["S.d" , "B.e"] ]

		ArrayList<LogicalOperator> newJoin = new ArrayList<LogicalOperator>();
		int joinSize = prevOptPlan.getTotalTuples()*topMostPlan.getTotalTuples();    // compute intermediate relation size
		PlanInfo newPlan = new PlanInfo(newJoin);
		String rightMostTable= topMostPlan.getAliasNames().get(0);
		ArrayList<String> newAliasNames = new ArrayList<String>();
		

		// if there are two table join :  choose the smaller size to be the left table
		if(prevOptPlan.getNumOps()==1) {
			if(prevOptPlan.getTotalTuples()<topMostPlan.getTotalTuples()) {
				newJoin=prevOptPlan.getOpsCopy();
				newJoin.addAll(topMostPlan.getOpsCopy());
				newAliasNames = prevOptPlan.getAliasNames();
				newAliasNames.add(rightMostTable);

			}else {
				newJoin=topMostPlan.getOpsCopy();
				newJoin.addAll(prevOptPlan.getOpsCopy());
				newAliasNames.add(rightMostTable);
				newAliasNames.addAll(prevOptPlan.getAliasNames());
			}
		}else {
			newJoin = prevOptPlan.getOpsCopy();
			newJoin.addAll(topMostPlan.getOpsCopy());
			newAliasNames.addAll(prevOptPlan.getAliasNames());
			newAliasNames.add(rightMostTable);
		}
		// if there exists a set of bluebox relating to left and right
		for(ArrayList<String> equ :equTableColSet) {
			int Vupdate=Integer.MAX_VALUE;  //3.4.4 specified: updating v is the min, so start with max and keep decrease until MIN
			int Vproduct = 1;               //3.4.3 specified: shrink size is the max amoung (V(R.A,S.C)), so start with 1, and keep increase
			// get the minimum column V
			for(String condition:equ) {
				String aliasName = condition.split("\\.")[0];
				String columnName = condition.split("\\.")[1];
				int curV;
				if(aliasName.equals(rightMostTable)) {
					curV=topMostPlan.getColV(aliasName, columnName);
				}else {
					curV=prevOptPlan.getColV(aliasName, columnName);
				}
				Vupdate = Math.min(Vupdate, curV);
				Vproduct = Math.max(Vproduct, curV);
			}
			joinSize = (int)joinSize/Vproduct;
			// set the V value of columns in newPlan  as   V(R,a)=V(R,h)=V(B,c)=min V = Vupdate  
			for(String condition:equ) {
				String aliasName = condition.split("\\.")[0];
				String columnName = condition.split("\\.")[1];
				newPlan.addColV(aliasName, columnName, Vupdate);
			}
		}
		newPlan.setOptimalOrder(newJoin);
		newPlan.setAliasName(newAliasNames);

		
		// filling the rest of cols V and compare with the new Tuple number
		HashMap<String, Integer> updateAll = prevOptPlan.copyColStas();
		updateAll.putAll(topMostPlan.copyColStas());
		for(String colKey:updateAll.keySet()) {
			String aliasName = colKey.split("\\.")[0];
			String columnName = colKey.split("\\.")[1];
			int updateV = Math.min(updateAll.get(colKey),joinSize);
			// will always add if smaller (enforced in PlanInfo)
			newPlan.addColV(aliasName, columnName, updateV);
		}
		int cost=prevOptPlan.getCost()+joinSize;
		newPlan.setCost(cost);
		newPlan.setTotalTuples(joinSize);
		return newPlan;
	}
}
