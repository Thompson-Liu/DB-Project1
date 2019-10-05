package logicalOperators;

public class DuplicateEliminationLogOp extends LogicalOperator {

	private LogicalOperator childOp;
	
	public DuplicateEliminationLogOp(LogicalOperator child) {
		childOp = child;
	}
	
	public LogicalOperator[] getChidren() {
		return new LogicalOperator[] { childOp }; 
	}
	
}
