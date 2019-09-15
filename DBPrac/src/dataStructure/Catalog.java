package dataStructure;

import java.util.ArrayList;
import java.util.HashMap;

public class Catalog {
	
	private static Catalog dbCatalog = null;
	private HashMap<String, String> tableDir;
	
	private Catalog() {
		tableDir = new HashMap<String, String>();
	}
	
	public static Catalog getInstance() {
		if (dbCatalog == null) {
			dbCatalog = new Catalog();
		}
		return dbCatalog;
	}
	
	public String getDir(String name) {
		return tableDir.get(name);
	}
	
	public void addDir(String name, String dir) {
		tableDir.put(name, dir);
	}

	
	public void printCatalog() {
		System.out.println("Tables directorys:");
		for(String table: tableDir.keySet()) {
			System.out.println(table+tableDir.get(table));
		}
	}
}
