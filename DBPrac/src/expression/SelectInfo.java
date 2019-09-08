package expression;
import dataStructure.*;
import java.util.*;

public class SelectInfo {
	
	private ArrayList<String> SelectCol;
	private ArrayList<String> From;  // a list of table names to choose from
//	Where;
	private DataTable where;
	
	
	public void setSelectCol(List l) {
		this.SelectCol=new ArrayList<String>(l);
	}
	
	public void SetFrom(List l) {
		this.From = new ArrayList<String>(l);
	}
	
	public void setWhere() {
		this.where = 
	}
	
	public void printSelectInfo(SelectInfo s) {
		System.out.println("Select Item "+Arrays.toString(SelectCol.toArray()));
	}
}
