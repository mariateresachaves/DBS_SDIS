package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Utils {

	/**
	 * Error codes
	 */
	public static final int ERR_WRONG_ARGS = 1;
	public static final int ERR_NUM_ARGS = 2;
	public static final int ERR_SENDING_ADV = 3;
	public static final int ERR_SETTING_ADV = 4;
	public static final int ERR_SETTING_SOCK = 5;
	public static final int ERR_PLATE_NUM = 6;
	public static final int ERR_RECEIVE = 7;
	public static final int ERR_SEND = 8;
	public static final int ERR_DEST_ADDR = 9;
	public static final int ERR_REGISTER_WRONG_ARGS = 10;
	public static final int ERR_LOOKUP_WRONG_ARGS = 11;
	public static final int ERR_LOGFILE = 12;
	public static final int ERR_OVER_CHUNK = 13;
	public static final int ERR_NULLBODY_CHUCK = 14;
	public static final int ERR_NULLSENDERID_CHUCK = 15;
	public static final int ERR_NULLFILEID_CHUCK = 16;
	public static final int ERR_SIZECHUNK_CHCONTROLLER = 17;
	public static final int ERR_REP_DEGREE = 18;

	/**
	 * Logger
	 */
	private static Logger log;
	private static Properties p;

	public static Logger getLogger() {
		if (log == null) {
			log = Logger.getLogger(Utils.class.getName());
			// Mudar nas props
			FileHandler f = null;
			try {
				f = new FileHandler(p.getProperty("LogFile", "mylog.log"));
			} catch (IOException ex) {
				System.err.println("[-] Could not create log file the program will terminate");
				System.exit(ERR_LOGFILE);
			} catch (SecurityException ex) {
				System.err.println("[-] Could not create log file the program will terminate");
				System.exit(ERR_LOGFILE);
			}

			log.addHandler(f);
		}
		return log;
	}

	public static Properties loadPropertiesFile(File f) throws FileNotFoundException, IOException {
		p = new Properties();
		InputStream inputStream;

		inputStream = Thread.currentThread().getContextClassLoader().getClass().getClassLoader()
				.getResourceAsStream(f.getPath());

		if (inputStream != null) {
			p.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + p + "' not found in the classpath");
		}
		return p;
	}

	public static Properties loadPropertiesFile(String file) throws FileNotFoundException, IOException {
		File f = new File(file);
		return loadPropertiesFile(f);
	}

	public static Properties getPropertie() {
		return p;
	}

}
