package fileIO;

import java.util.*;
import dataStructure.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/** log debug statement to a file */
public class Logger {

	private static Logger log= new Logger();

	private Logger() {
	}

	public static Logger getInstance() {
		if (log == null) {
			log= new Logger();
		}
		return log;
	}
	
	public void dumpTable(ArrayList<Tuple> table) {
		String fileName= "./log.txt";
		File tmp= new File(fileName);
		ReadableTupleWriter writer = new ReadableTupleWriter(fileName);
		writer.write(table);
		writer.dump();
		writer.close();
	}

	public void dumpMessage(String msg) {
		try {
		String fileName= "./log.txt";
		File tmp= new File(fileName);
		BufferedWriter writer;
		if (tmp.exists()) {
			writer= new BufferedWriter(new FileWriter(fileName, true));
			writer.append("\n");
			writer.append(msg);
		} else {
			writer= new BufferedWriter(new FileWriter(fileName));
			writer.write(msg);
		}
		writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
