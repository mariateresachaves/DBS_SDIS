package Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import Service.Listeners.MCCListener;
import Service.Listeners.MDBListener;
import Service.Listeners.MDRListener;
import Utils.Util;
import Utils.Util.ErrorCode;

public class Peer {

	// Chanel Listeners definition
	public static MCCListener mccl;
	public static MDBListener mdbl;
	public static MDRListener mdrl;

	// XML Database
	public static XMLDatabase xmldb;

	private static ShellInterpreter shell = new ShellInterpreter();

	private static final HashMap<String, ArrayList<StoredChunk>> database = new HashMap<>();

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: Peer <PropertiesFile>");
			Util.getLogger().log(Level.SEVERE, "Invalid arguments at the start of application\n");
			System.exit(ErrorCode.ERR_WRONG_ARGS.ordinal());
		}

		// Load properties file
		Util.loadPropertiesFile(args[0]);

		// Overload Properties Defined in File
		if (args.length == 2) {
			// Set ServiceID
			Util.getProperties().setProperty("SenderID", args[1]);
		}

		// XMLDatabase
		xmldb = new XMLDatabase(Util.getProperties().getProperty("XMLDB", "./xmldb.xml"));

		// Start Mulicast Data Backup Listener
		mdbl = new MDBListener();
		Thread t = new Thread(mdbl);
		t.start();

		// Start Mulicast Control Channel Listener
		mccl = new MCCListener();
		Thread t1 = new Thread(mccl);
		t1.start();

		// Start Mulicast Data Restore Listener
		mdrl = new MDRListener();
		Thread t2 = new Thread(mdrl);
		t2.start();

<<<<<<< HEAD

=======
>>>>>>> f6f9554a81c30699a04c63c248e02263dd5d49c3
		// Shell Interpreter
		shell.getShell();

	}

	public static HashMap<String, ArrayList<StoredChunk>> getDB() {
		return database;
	}

	public static void saveChunkToDB(String filePathName, ArrayList<StoredChunk> storedChunks) {
		database.put(filePathName, storedChunks);
	}

	public static ArrayList<StoredChunk> getStoredChunk(String filePathName) {
		return database.get(filePathName);
	}
}
