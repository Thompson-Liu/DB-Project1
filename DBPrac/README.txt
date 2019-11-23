The top-level class of our code is src/controllers/Interpreter.java, which reads the input and produces output. 

- Push Select Conditions:
There are two parts of select conditions, first is the conditions that can be included in a bluebox, like Sailors.A = Sailors.B; the other kind is those conditions that cannot go into bluebox, like Sailors.A != 30 or Sailors.C != Sailors.B. So, to select on each table in LogicalOperatorFactory, we will search through each bluebox for column attributes that have the same tableName. Then we will build a partial select expression from each bluebox. For example, if the bluebox contains Sailors.A, Sailors.B, and the bounds are null and 149, then the condition built is Sailors.A = Sailors.B and. Sailors.B <= 149. The expression is built by looping through each column attributes in the bluebox and construct an AndExpression between the two columns. If the last column attribute is met, then we will construct a MinorThanEqual, or GreaterThanEqual, or both, which depends on the existence of both upper and lower bounds. If both bounds are not null, then both MinorThanEqual and GreaterThanEqual will be constructed and connected by an AndExpression. 

Then the other part of the select expression comes from those that cannot go into bluebox. A visitor is used to visit on the usedExpression to find the ones that are relevant to the current table. If it's not null, we will connect it with the expression parsed from the unionFind result by an AndExpression. This is the select condition that's pushed down to the current table before executing join. 



- Implement Logical Selection Operator: 
If the combined expression is null, then physicalPlanBuilder will construct a fullScanOperator. Otherwise, we will calculate the cost as follows to choose between full scan, indexScan, and which column to use as the index column: 

The SelectCost.selectScan method will return a String array of size 3. If an indexScan is chosen, then the returned array is ["index", "indexCol", "clustered"], the index column will be removed from the associated bluebox, so that when we construct the expression in LogicalOperatorFactory, no attributes will be constructed about this index column. However, if there are more than two columns in the bluebox, like Sailors. A, Sailors.B, and the index column is Sailors.A, then the bluebox will contain only Sailors.B. In order to ensure the equality between Sailors.A and Sailors.B, we will reinsert an extra equal condition between Sailors.A and Sailors.B, which is the select condition of the Select physical Operator and it has an IndexScan child operator that indexes on Sailors.B

If a fullScan is chosen, then the returned array is ["full", "", ""]. A full scan will not take any select condition, and all the select conditions will be added to construct a Select physical operator, which is the parent class of the full scan physical operator. 

For fullScan the cost is total number of tuples, and for index scan 





Refactor logical and physical query plans:
We refactored our logicalOperatorFactory to construct query plans.

TableStas: 
we refactored our catalog to maintain more information of join tuple.

Join Optimizer:
We compute the cost of join in Dynamic Programing in bottom-up sequence. The data structure we use



- Choice of each join operator:
In Project2, we experimented and found out that SMJ usually have better performance than BNLJ. Therefore, we will check for select conditions: (1) if it contains only EqualsToExpression, (2) if it's applicable to use SMJ. When checking expressions, it takes the list of tableNames that are already joined together, and the name of the table that is being joined. If the joined expression between these two tables only contains an equality condition, that has one side included in the joined tableName list, and the other side equals the tableName being joined, then we will construct a SMJ. Otherwise, if the expression is an instance of AndExpression, ie. more than two conditions, or no conditions at all, it's not applicable to use SMJ, so a BNLJ will be constructed.
