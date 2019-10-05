package fileIO;

/**
 *  log debug statement to a file
 *
 */
public class Logger {
	
	private static Logger log = null;
	
	private Logger() {
		
	}

	public static Logger getInstance() {
		if (log == null) {
			log = new Logger();
		}
		return log;
	}

}
