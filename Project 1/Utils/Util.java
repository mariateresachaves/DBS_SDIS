package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

	/**
	 * Error codes
	 */
	public enum ErrorCode {
		ERR_WRONG_ARGS,
		ERR_NUM_ARGS,
		ERR_SENDING_ADV,
		ERR_SETTING_ADV,
		ERR_SETTING_SOCK,
		ERR_PLATE_NUM,
		ERR_RECEIVE,
		ERR_SEND,
		ERR_DEST_ADDR,
		ERR_REGISTER_WRONG_ARGS,
		ERR_LOOKUP_WRONG_ARGS,
		ERR_LOGFILE,
		ERR_OVER_CHUNK,
		ERR_NULLBODY_CHUCK,
		ERR_NULLSENDERID_CHUCK,
		ERR_NULLFILEID_CHUCK,
		ERR_SIZECHUNK_CHCONTROLLER,
		ERR_REP_DEGREE,
		ERR_NO_PROTO,
		ERR_CHUNKSTORAGE,
		ERR_CREATELISTMDB,
		ERR_SENDSTORED,
		ERR_CREATELISTMDR,
		ERR_CREATELISTMCC,
		ERR_SOCKET_TIMEOUT,
		ERR_MCC_PACKET
		
	}
	
	/**
	 * Logger
	 */
	private static Logger log;
	private static Properties p = new Properties();

	public static Logger getLogger() {
		if (log == null) {
			log = Logger.getLogger(Util.class.getName());
			// Mudar nas props
			FileHandler f = null;
			try {
				f = new FileHandler(p.getProperty("LogFile", "mylog.log"));
			} catch (IOException ex) {
				System.err.println("[-] Could not create log file the program will terminate");
				System.exit(ErrorCode.ERR_LOGFILE.ordinal());
			} catch (SecurityException ex) {
				System.err.println("[-] Could not create log file the program will terminate");
				System.exit(ErrorCode.ERR_LOGFILE.ordinal());
			}

			log.addHandler(f);
		}
		return log;
	}

	public static Properties loadPropertiesFile(File f) throws FileNotFoundException, IOException {
		p = new Properties();
		InputStream inputStream;

		inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(f.getPath());

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

	public static Properties getProperties() {
		return p;
	}

	public void saveProperties(File f) {
		try {

			OutputStream out = new FileOutputStream(f);
			p.store(out, "Saved" + Calendar.getInstance().getTime());
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Couldn't Save Properties File");
		}
	}

}
