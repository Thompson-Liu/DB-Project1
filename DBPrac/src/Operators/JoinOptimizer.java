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
import logicalOperators.JoinLogOp;
import logicalOperators.LogicalOperator;
import logicalOperators.SelectLogOp;
import net.sf.jsqlparser.expression.Expression;
import physicalOperator.JoinOperator;
import physicalOperator.Operator;
import physicalOperator.SelectOperator;

public class JoinOptimizer {
	private Expression joinExp;
	private ArrayList<String> tableNames;
	private ArrayList<Operator> baseOperators;     // one-to-one position corresponding with tableName above
	private Catalog cat;
	private HashMap<String,String> alias;                          // 🌟<tableName,alias>    需要double check现在的alias是否是这个方向㊗
	private HashMap<HashSet<String> , PlanInfo> subsetPlan;        // store the optimal subset and corresponding information

	public JoinOptimizer(ArrayList<String> tableNames,ArrayList<Operator> baseOps, Expression joinExpr) {
		subsetPlan= new HashMap<HashSet<String>, PlanInfo>();
		cat=Catalog.getInstance();
		this.joinExp=joinExpr;
		this.tableNames = tableNames;
		this.baseOperators  = baseOps;
	}

	/**  Compute cost of join in a bottom-up fashion using Dynamic Programming. */
	public Operator findOptimalJoinOrder() {
		//start from base case bottom up until the the construction contains whole
		for(int subPlanSize=1;subPlanSize<=tableNames.size();subPlanSize++){
			// each table could be the top most table in the subPlan
			for (int topMost= 0; topMost < tableNames.size(); topMost++ ) {
				//base case
				if(subPlanSize==1) {
					Operator cur = this.baseOperators.get(topMost);
					if(cur instanceof SelectOperator) {
						selectionVupdate(cur);
					}else {
						baseTableVupdate(cur);}
				}else {
					ArrayList<String> prevSub=new ArrayList<String>();  // track the previous traversed subPlans
					traverseSubPlans(topMost,subPlanSize,1,0,prevSub);}
			}
		}
		HashSet<String> prevSubTables = new HashSet<String>(this.tableNames);
		PlanInfo optimalPlan = this.subsetPlan.get(prevSubTables);
		return optimalPlan.getOp();
	}

	/**
	 *  get all the possible combination in (targetSize), using topMost as the right most table	
	 *  update the subset plan of {T1, ...T_(size-1),topMost} tables with the optimal operator joining tree	
	 * @param topMost        index of right most table to start
	 * @param targetSize     total size of this subPlan
	 * @param curLen         lenghth of the cumulated tables
	 * @param curPos         pos of the next table to be added
	 * @param prevSubPlan    array of tables
	 */
	public void traverseSubPlans(int topMost,int targetSize, int curLen,int curPos,ArrayList<String> prevSubPlan) {
		// get one set of size=subPlanSize, compute cost of joining topMost with the best plan of this set
		if(prevSubPlan.size()==targetSize) {
			ArrayList<String> prevSubCopy = new ArrayList<String>();
			prevSubCopy.addAll(prevSubPlan);
			HashSet<String> prevSubTables = new HashSet<String>(prevSubCopy);
			PlanInfo prevOptPlan = this.subsetPlan.get(prevSubTables);
			HashSet<String> topMostTable = new HashSet<String>();
			PlanInfo topMostPlan = this.subsetPlan.get(topMostTable);
			//TODO 
			// 1. update and calculate new cost
			// 2. create a new Plan Info and added to
			// 3. get join expression for this new plan
			// 4. create this logical operator either scan or select
			PlanInfo newPlan = joinVupdate(prevOptPlan,topMostPlan);
			prevSubTables.add(this.tableNames.get(topMost));
			//update the hashMap if this plan is the optimal cost among operators with same set of tables 
			//or it is the first time to process this set of tables
			Boolean condition = !subsetPlan.containsKey(prevSubTables) || subsetPlan.get(prevSubTables).getCost()>newCost;
			if(condition) {
				this.subsetPlan.put(prevSubTables, newPlan);
			}
			return;
		}
		// Skip if this new table is the topMost table
		else if (curPos==topMost) {
			curPos+=1;
			traverseSubPlans(topMost, targetSize,curLen,curPos,prevSubPlan);
		}
		// Terminate, if no more table to join or current table size in plan exceeds
		else if (curPos==this.tableNames.size() || prevSubPlan.size()>targetSize) {
			return;
		}
		// Recursive: for each curPos, 2 cases: either select it / not select it
		else {
			curPos+=1;
			traverseSubPlans(topMost,targetSize,curLen,curPos,prevSubPlan);
			ArrayList<String> addedSubPlan = new ArrayList<String>();
			addedSubPlan.addAll(prevSubPlan);
			traverseSubPlans(topMost,targetSize,curLen++,curPos,addedSubPlan);
		}
	}


	/** helper method to set the V of baseTable and the total number of tuple
	 * @param Operator    either scan or indexScan on base(Table) 
	 */
	private void baseTableVupdate(Operator baseOp) {
		Catalog catalog = Catalog.getInstance();
		String tableName = baseOp.getTableName();
		String aliasName = alias.get(tableName);
		int numTuples = catalog.getTupleNums(tableName);
		PlanInfo basePlan = new PlanInfo(baseOp);
		ArrayList<String> schema = catalog.getSchema(tableName);
		for(String col:schema) {
			//V(R,A)=max- min +1
			int v = catalog.getColRange(tableName, col)[1]-catalog.getColRange(tableName, col)[0]+1;
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
	private void selectionVupdate(Operator selectOp) {
		Catalog catalog = Catalog.getInstance();
		String tableName = selectOp.getTableName();
		String aliasName = alias.get(tableName);
		ArrayList<String> schema = catalog.getSchema(tableName);
		int numTuples = catalog.getTupleNums(tableName);
		// TODO (call UnionFind function,e.g. getTableColRange(TableName) to get)
		HashMap<String,int[]> colRange;

		// setup the PlanInfo of this select operator
		PlanInfo curPlan = new PlanInfo(selectOp);
		double reductionFactor =1;
		for(String col:schema) {
			int oriHigh = catalog.getColRange(tableName, col)[1];
			int oriLow=catalog.getColRange(tableName, col)[0];
			if(colRange.containsKey(col)) {  // check if col is in expr {R.A=3 or R.A<4} with low and high bound
				int newHigh = Math.min(colRange.get(col)[1],oriHigh);     // 注: union find return intMax if no upper bound
				int newLow = Math.max(colRange.get(col)[0], oriLow);
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
		int newNum = (int) (reductionFactor*numTuples);
		newNum = (newNum==0) ?  1:newNum;      // total number of tuples round to 1 if zero
		curPlan.setCost(0);
		curPlan.setTotalTuples(newNum);
		// update the V(R,A) , if there is less tuples than the V
		for(String col:schema) {
			if(curPlan.getColV(aliasName, col)>newNum) {
				curPlan.addColV(aliasName, col, newNum);
			}
		}
		HashSet<String> curTable = new HashSet<String>();
		curTable.add(aliasName);
		this.subsetPlan.put(curTable,curPlan);
	}

	private PlanInfo joinVupdate(PlanInfo prevOptPlan,PlanInfo topMostPlan) {
		// TODO           call and get the joining expression and joining columns from Union Find
		Expression curJoinExp;      
		ArrayList<ArrayList<String>> equTableColSet;  
		//👆 Alias+col : equalities among a list of tables since prevOptPlan contains multiple tables
		//  e.g.   " R.a=R.h=B.C  AND  S.d=B.e "    prevTables:[R,S]  topMostTable: [B]
		//      WANT:  [ ["R.a","R.h" , "B.c"], ["S.d" , "B.e"] ]
		// little change:如果一个column和很多个其他的column都相等，放到一个arraylist里
		
		
		JoinOperator newJoin = new JoinOperator(prevOptPlan.getOp(),topMostPlan.getOp(),curJoinExp);
		PlanInfo newPlan = new PlanInfo(newJoin);
		String rightMostTable= alias.get(topMostPlan.getOp().getTableName());
		int joinSize = prevOptPlan.getTotalTuples()*topMostPlan.getTotalTuples();    // compute intermediate relation size
		// if there exists a set of bluebox relating to left and right
		for(ArrayList<String> equ :equTableColSet) {
			int Vupdate=Integer.MAX_VALUE;  //3.4.4 specified: updating v is the min, so start with max and keep decrease until MIN
			int Vproduct = 1;               //3.4.3 specified: shrink size is the max amoung (V(R.A,S.C)), so start with 1, and keep increase
			// get the minimum column V
			for(String condition:equ) {
				String aliasName = condition.split("//.")[0];
				String columnName = condition.split("//.")[1];
				int curV;
				if(aliasName==rightMostTable) {
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
				String aliasName = condition.split("//.")[0];
				String columnName = condition.split("//.")[1];
				newPlan.addColV(aliasName, columnName, Vupdate);
			}
		}
		
		// filling the rest of cols V and compare with the new Tuple number
		int cost=prevOptPlan.getCost()+joinSize;
		newPlan.setCost(cost);
		newPlan.setTotalTuples(joinSize);
		
		
		return newPlan;

	}
}
