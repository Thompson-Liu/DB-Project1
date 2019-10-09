package fileIO;

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

	public void dumpMessage(String msg) throws IOException {
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
	}

}
