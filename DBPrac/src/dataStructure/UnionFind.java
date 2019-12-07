package dataStructure;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

/** This class implements the union find algorithm. An instance is a UnionFind object containing
 * blue boxes generated from the queries. */
public class UnionFind {

	private List<BlueBox> elements;

	/** Initialize class fields. */
	public UnionFind() {
		elements= new ArrayList<BlueBox>();
	}

	public UnionFind(List<BlueBox> uf) {
		elements = new ArrayList<BlueBox>();
		for (BlueBox bb: uf) {
			elements.add(bb.copy());
		}
	}

	/** Copy constructor  **/
	public UnionFind(UnionFind ufCopy) {
		elements = new ArrayList<BlueBox>();
		for (BlueBox bb: ufCopy.getBlueBoxes()) {
			elements.add(bb.copy());
		}
	}

	/**
	 * Build expressions from the bluebox that contains attributes of the table
	 * 
	 * @param selectAttr 	a list of bluebox which contains equality between column attributs of the current table
	 * @return        		an expression built from the list of blueboxes
	 */
	public Expression buildExpression() {
		// If the hashMap is empty, all attributes are resolved, return a null Expression
		if (elements.isEmpty()) {
			return null;
		}

		List<Expression> intermediate = new ArrayList<Expression>();
		for (BlueBox bb: elements) {
			// Initialize a null value to start the rescurssion 
			intermediate.add(buildCol(new NullValue(), bb.getAttr(), bb.getBound()));
		}

		if (intermediate.isEmpty()) {
			return null;
		} else if (intermediate.size() == 1) {
			return intermediate.get(0);
		}

		Expression result = new AndExpression(intermediate.remove(0), intermediate.remove(0));
		return buildExpressionHelper(result, intermediate); 
	}

	/**
	 * A helper to connect each intermediate expressions with an AndExpression. 
	 * 
	 * @param intermediate   Intermediate result of the recurssive step
	 * @param expr           List of expression parsed before that need to be connected together
	 * @return				 A connected expression
	 */
	private Expression buildExpressionHelper(Expression intermediate, List<Expression> expr) {
		if (expr.size() == 0) {
			return intermediate;
		}
		return buildExpressionHelper(new AndExpression(intermediate, expr.remove(0)), expr);
	}

	/**
	 * Construct a series of column Expression from the list of equal column attributes, 
	 * and also the boundaries on them too.
	 * 
	 * @param intermediate		Intermediate result of the recurssive step
	 * @param list				a list of equal column attributes	
	 * @param stats				the bound on the equal column attributes
	 * @return
	 */
	private Expression buildCol(Expression intermediate, List<String> list, Integer[] stats) {
		//  Construct a left Column object
		String left = list.remove(0);
		String leftTableName = left.split("\\.")[0];
		String leftTableCol = left.split("\\.")[1];

		Column leftCol = new Column();
		leftCol.setColumnName(leftTableCol);
		Table leftTable = new Table();
		leftTable.setName(leftTableName);
		leftCol.setTable(leftTable);

		// if there's only one attrs left, construct a attr op val
		if (list.size() == 0) {
			if (stats[0] == null && stats[1] == null) {
				// This case shouldn't happen
				assert(! (intermediate instanceof NullValue));
				return intermediate;
			} 

			if (stats[0] == null && stats[1] != null) {
				MinorThanEquals minorEqual = new MinorThanEquals();
				minorEqual.setLeftExpression(leftCol);
				minorEqual.setRightExpression(new LongValue(stats[1].toString()));
				if (intermediate instanceof NullValue) {
					return minorEqual;
				}
				return new AndExpression(intermediate, minorEqual);
			}

			if (stats[0] != null && stats[1] == null) {
				GreaterThanEquals greaterEqual = new GreaterThanEquals();
				greaterEqual.setLeftExpression(leftCol);
				greaterEqual.setRightExpression(new LongValue(stats[0].toString()));
				if (intermediate instanceof NullValue) {
					return greaterEqual;
				}
				return new AndExpression(intermediate, greaterEqual);
			}

			if (stats[0] == stats[1]) {
				EqualsTo equalExpr = new EqualsTo();
				equalExpr.setLeftExpression(leftCol);
				equalExpr.setRightExpression(new LongValue(stats[0].toString()));
				if (intermediate instanceof NullValue) {
					return equalExpr;
				}
				return new AndExpression(intermediate, equalExpr);
			}
			GreaterThanEquals greaterEqual = new GreaterThanEquals();
			greaterEqual.setLeftExpression(leftCol);
			greaterEqual.setRightExpression(new LongValue(stats[0].toString()));

			MinorThanEquals minorEqual = new MinorThanEquals();
			minorEqual.setLeftExpression(leftCol);
			minorEqual.setRightExpression(new LongValue(stats[1].toString()));
			if (intermediate instanceof NullValue) {
				return new AndExpression(minorEqual, greaterEqual);
			}
			return new AndExpression(new AndExpression(intermediate, greaterEqual), minorEqual);
		}

		// Otherwise, construct the right Column object
		String right = list.get(0);
		String rightTableName = right.split("\\.")[0];
		String rightTableCol = right.split("\\.")[1];

		Column rightCol = new Column();
		rightCol.setColumnName(rightTableCol);
		Table rightTable = new Table();
		rightTable.setName(rightTableName);
		rightCol.setTable(rightTable);

		// Construct the equality between the two column
		EqualsTo colEqual = new EqualsTo();
		colEqual.setLeftExpression(leftCol);
		colEqual.setRightExpression(rightCol);

		if (intermediate instanceof NullValue) {
			return buildCol(colEqual, list, stats);
		}
		return buildCol(new AndExpression(intermediate, colEqual), list, stats);
	}

	/** Find the bluebox that col is in. If no such box exists, create a new bluebox containing col.
	 * 
	 * @param String col: the name of the column to look for
	 * @return a bluebox whose attribute list contains col */
	public BlueBox find(String col) {
		for (BlueBox bb : elements) {
			if (bb.getAttr().contains(col)) { return bb; }
		}
		List<String> attributes= new ArrayList<String>();
		attributes.add(col);
		BlueBox newBB = new BlueBox(attributes);
		elements.add(newBB);
		return newBB;
	}

	public List<BlueBox> getBlueBoxes() {
		return elements;
	}

	public Boolean isEmpty() {
		return elements.isEmpty();
	}

	/** Example: WHERE S.A = S.B AND S.B = S.C AND S.D = 15 Return: <[S.A, S.B, S.C]: [Null, Null]>,
	 * <[S.D]: [15,15]>
	 * 
	 * @param alias
	 * @return */
	public UnionFind findSelect(String alias) {
		List<BlueBox> relevantBBList = new ArrayList<BlueBox>();
		List<String> attrs = new ArrayList<String>();

		// Loop through each bluebox stored
		for (BlueBox bb : elements) {
			List<String> attributes= bb.getAttr();

			// Loop through the attributes list and try to find the attributes starting with alias
			for (int i= 0; i < attributes.size(); ++i) {
				String attr= attributes.get(i);
				String tableName= attr.split("\\.")[0];
				if (tableName.equals(alias)) {
					attrs.add(attr);
				}
			}

			boolean noBound = (bb.getLower() == null) && (bb.getUpper() == null);
			// if the attributes in this bluebox are not empty, add the arraylist into the hashmap
			if (attrs.size() > 1 || (attrs.size() == 1 && !noBound))  {
				BlueBox relevantBB = new BlueBox(attrs);
				if (bb.getLower() != null) {
					relevantBB.setLower(bb.getLower());
				}
				if (bb.getUpper() != null) {
					relevantBB.setUpper(bb.getUpper());
				} 
				if (bb.getEqual() != null) {
					relevantBB.setEqual(bb.getEqual());
				}
				relevantBBList.add(relevantBB);
			}
			attrs= new ArrayList<String>();
		}
		return new UnionFind(relevantBBList);
	}

	/** Example: " R.a=R.h=B.C AND S.d=B.e " prevTables:[R,S] topMostTable: [B] Return: [ ["R.a","R.h" ,
	 * "B.c"], ["S.d" , "B.e"] ]
	 * 
	 * @param tableList
	 * @param table2
	 * @return */
	public List<ArrayList<String>> findJoin(List<String> tableList, String table2) {
		List<ArrayList<String>> joinCondition= new ArrayList<ArrayList<String>>();

		for (BlueBox bb : elements) {
			List<String> attributes= bb.getAttr();

			// Loop through the attributes list and try to find the attributes starting with alias
			boolean found1= false;
			boolean found2= false;

			ArrayList<Integer> table1Indexes= new ArrayList<Integer>();
			ArrayList<Integer> table2Indexes= new ArrayList<Integer>();

			for (int i= 0; i < attributes.size(); ++i) {
				String attr= attributes.get(i);
				String tableName= attr.split("\\.")[0];
				if (tableList.contains(tableName)) {
					found1= true;
					table1Indexes.add(i);
				}
				if (tableName.equals(table2)) {
					found2= true;
					table2Indexes.add(i);
				}
			}

			// If there are attributes that match both table1 and table2, add them to a new arraylist
			if (found1 && found2) {
				ArrayList<String> attrList= new ArrayList<String>();
				for (Integer index : table1Indexes) {
					attrList.add(attributes.get(index));
				}

				for (Integer index : table2Indexes) {
					attrList.add(attributes.get(index));
				}
				joinCondition.add(attrList);
			}

		}
		return joinCondition;
	}

	/** Example: " R.a=R.h=B.C AND S.d=B.e " prevTables:[R,S] topMostTable: [B] Return: [[R.a, B.c],
	 * [S.d, B.e]]
	 * 
	 * @param tableLeftList
	 * @param table2
	 * @return */
	public List<ArrayList<String>> findJoinInBox(List<String> tableLeftList, String table2) {
		List<ArrayList<String>> joinCondition= new ArrayList<ArrayList<String>>();

		for (BlueBox bb : elements) {
			// check if equality, both lower and upper will be set
			if (bb.getEqual() != null) {
				continue;
			}

			List<String> attributes= bb.getAttr();

			// Loop through the attributes list and try to find the attributes starting with alias
			boolean foundLeftList= false;
			boolean foundTableRight= false;
			int leftListIndex= -1;
			int rightTableIndex= -1;

			for (int i= 0; i < attributes.size(); ++i) {
				if (foundLeftList && foundTableRight) {
					break;
				}

				String attr= attributes.get(i);
				String tableName= attr.split("\\.")[0];
				if (tableLeftList.contains(tableName)) {
					foundLeftList= true;
					leftListIndex= i;
				}
				if (tableName.equals(table2)) {
					foundTableRight= true;
					rightTableIndex= i;
				}
			}

			// If there are attributes that match both table1 and table2, add them to a new arraylist
			if (foundLeftList && foundTableRight) {
				ArrayList<String> attrList= new ArrayList<String>();
				attrList.add(attributes.get(leftListIndex));
				attrList.add(attributes.get(rightTableIndex));

				joinCondition.add(attrList);
			}
		}
		return joinCondition;
	}

	/** Merge two blue boxes. Modify the UnionFind data structure in place.
	 * 
	 * @param BlueBox b1, BlueBox b2: two blue boxes to be merged */
	public void merge(BlueBox b1, BlueBox b2) {
		// Do we need to check if b1 and b2 are the same blue box
		List<String> attributes= new ArrayList<String>(b1.getAttr());
		attributes.addAll(b2.getAttr());
		BlueBox merged= new BlueBox(attributes);

		// Keep blue box constraints true
		// low
		if (b1.getLower() != null && b2.getLower() != null) {
			merged.setLower(Math.max(b1.getLower(), b2.getLower()));
		} else if (b1.getLower() != null && b2.getLower() == null) {
			merged.setLower(b1.getLower());
		} else if (b1.getLower() == null && b2.getLower() != null){
			merged.setLower(b2.getLower());
		} 


		// high
		if (b1.getUpper() != null && b2.getUpper() != null) {
			merged.setUpper(Math.min(b1.getUpper(), b2.getUpper()));
		} else if (b1.getLower() != null && b2.getUpper() == null) {
			merged.setUpper(b1.getUpper());
		} else if (b1.getLower() == null && b2.getLower() != null){
			merged.setUpper(b2.getUpper());
		}

		// equal
		if (b1.getEqual() != null) {
			merged.setEqual(b1.getEqual());
		} else if (b2.getEqual() != null) {
			merged.setEqual(b2.getEqual());
		}

		// remove box1 and box2
		elements.add(merged);
		elements.remove(b1);
		elements.remove(b2);
	}

	/** Set the lower bound field for bluebox box to be lo
	 * 
	 * @param box: the bluebox to be reset
	 * @param lo: the new lower bound */
	public void setLower(BlueBox box, int lo) {
		box.setLower(lo);
	}

	/** Set the upper bound field for bluebox box to be up
	 * 
	 * @param box: the bluebox to be reset
	 * @param up: the new upper bound */
	public void setUpper(BlueBox box, int up) {
		box.setUpper(up);
	}

	/** Set the equality field for bluebox box to be eq
	 * 
	 * @param box: the bluebox to be reset
	 * @param eq: the new equality bound */
	public void setEqual(BlueBox box, int eq) {
		box.setEqual(eq);
	}

}
