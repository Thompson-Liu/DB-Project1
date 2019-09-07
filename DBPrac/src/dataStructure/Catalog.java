package dataStructure;

import java.util.HashMap;

public class Catalog {
	
	private static Catalog dbCatalog = null;
	private HashMap<String, String> tableDir;
	private HashMap<String, String[]> tableSchema;
	
	private Catalog() {
		tableDir = new HashMap<String, String>();
		tableSchema = new HashMap<String, String[]>();
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
	
	public String[] getSchema(String name) {
		return tableSchema.get(name);
	}
	
	public void addDir(String name, String dir) {
		tableDir.put(name, dir);
	}
	
	public void addSchema(String name, String[] schema) {
		tableSchema.put(name, schema);
	}
}
