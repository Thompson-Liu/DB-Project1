package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import dataStructure.Catalog;

public class CatalogGenerator {
	
	String dbDir; 
	
	public CatalogGenerator(String dbDir) {
		this.dbDir = dbDir;
	}
	
	/** Construct catalog from directory
	 * 
	 * @param directory directory to find the schema file
	 * @return the schema */
	public void createCatalog() {
		Catalog cat= Catalog.getInstance();
		try {
			FileReader schemafw= new FileReader(dbDir + "/schema.txt");
			BufferedReader readSchema= new BufferedReader(schemafw);
			String line;
			while ((line= readSchema.readLine()) != null) {
				String[] schemaLine= line.trim().split("\\s+");
				String tableName= schemaLine[0];
				cat.addDir(tableName,  dbDir + "/data/" + tableName);
				ArrayList<String> schem= new ArrayList<String>();
				for (int i= 1; i < schemaLine.length; i++ ) {
					schem.add(schemaLine[i]);
				}
				cat.addSchema(tableName, schem);
			}
			readSchema.close();
		} catch (IOException e) {
			System.err.println("Exception unable to access the directory");
		} finally {
			readSchema.close();
		}
	}
}
