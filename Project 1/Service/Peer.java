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

	private static Server mc_server;
	private static int mc_rate;
	private static String mc_ip;
	private static int mc_port;
	private static String mdb_ip;
	private static int mdb_port;
	private static String mdr_ip;
	private static int mdr_port;

	private static NodeCollector mc_collector;
	private static NodeCollector mdb_collector;
	private static NodeCollector mdr_collector;

	private static ShellInterpreter shell = new ShellInterpreter();

	private static final HashMap<String, ArrayList<StoredChunk>> database = new HashMap<>();

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: Peer <PropertiesFile>");
			Util.getLogger().log(Level.SEVERE, "Invalid arguments at the start of application");
			System.exit(ErrorCode.ERR_WRONG_ARGS.ordinal());
		}
		
		// Load properties file
		Util.loadPropertiesFile(args[0]);

		// Start Mulicast Data Backup Listener
		MDBListener mdbl = new MDBListener();
		Thread t = new Thread(mdbl);
		t.start();

		// Start Mulicast Control Channel Listener
		MCCListener mccl = new MCCListener();
		Thread t1 = new Thread(mccl);
		t1.start();

		// Start Mulicast Data Restore Listener
		MDRListener mdrl = new MDRListener();
		Thread t2 = new Thread(mdrl);
		t2.start();

		// Get multicast channels properties
		mc_rate = Integer.parseInt(Util.getProperties().getProperty("MC_RATE"));

		mc_ip = Util.getProperties().getProperty("MC_IP");
		mc_port = Integer.parseInt(Util.getProperties().getProperty("MC_PORT"));

		mdb_ip = Util.getProperties().getProperty("MDB_IP");
		mdb_port = Integer.parseInt(Util.getProperties().getProperty("MDB_PORT"));

		mdr_ip = Util.getProperties().getProperty("MDR_IP");
		mdr_port = Integer.parseInt(Util.getProperties().getProperty("MDR_PORT"));

		// Multicast Control Channel
		mc_server = new Server(mc_ip, mc_port, mc_rate);
		mc_server.createMenance();

		mc_collector = new NodeCollector(mc_ip, mc_port);

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
