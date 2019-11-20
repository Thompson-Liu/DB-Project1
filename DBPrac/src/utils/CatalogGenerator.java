package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import dataStructure.Catalog;
import dataStructure.Tuple;
import fileIO.BinaryTupleReader;

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
			BinaryTupleReader tableread;
			//write to file
			BufferedWriter writer= new BufferedWriter(new FileWriter(dbDir +"/stats.txt"));
			String nextLine= readSchema.readLine();
			while (nextLine != null && nextLine !="") {
				String[] schemaLine= nextLine.trim().split("\\s+");
				String tableName= schemaLine[0];
				String tableDir = dbDir + "/data/" + tableName;
				cat.addDir(tableName,  tableDir);
				writer.write(tableName+" ");

				ArrayList<String> schem= new ArrayList<String>();
				for (int i= 1; i < schemaLine.length; i++ ) {
					schem.add(schemaLine[i]);
				}
				cat.addSchema(tableName, schem);

				//read table
				tableread = new BinaryTupleReader(tableDir);
				int numTuples=0;
				int[][] colRange = new int[schem.size()][2];  //[low,high]
				for(int i=0;i<schem.size();i++) {
					colRange[i][0] =Integer.MAX_VALUE; 
					colRange[i][1]=Integer.MIN_VALUE;}
				Tuple cur=tableread.readNextTuple();
				while(cur!=null) {
					numTuples++;
					for(int i=0;i<schem.size();i++) {
						colRange[i][0] = Math.min(colRange[i][0], cur.getData(i));
						colRange[i][1] = Math.max(colRange[i][1],cur.getData(i));
					}
					cur=tableread.readNextTuple();
				}
				cat.setTupleNums(tableName, numTuples);
				writer.write(Integer.toString(numTuples)+" ");
				for(int i=0;i<schem.size();i++) {
					cat.addColRange(tableName, schem.get(i), colRange[i]);
					writer.write(schem.get(i)+",");
					writer.write(Integer.toString(colRange[i][0])+",");
					writer.write(Integer.toString(colRange[i][1])+" ");
				}
				writer.newLine();
				tableread.close();	
				nextLine= readSchema.readLine();
			}


			writer.close();
			readSchema.close();
		} catch (IOException e) {
			System.err.println("Exception unable to access the directory");
		}
	}
}
