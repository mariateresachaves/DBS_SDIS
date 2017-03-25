package Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import Service.Listeners.MDBListener;
import Utils.Util;

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

	private static final HashMap<String, StoredChunk> database = new HashMap<>();

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: Peer <PropertiesFile>");
			Util.getLogger().log(Level.SEVERE, "Invalid arguments at the start of application");
			System.exit(Util.ERR_WRONG_ARGS);
		}

		// Load properties file
		Util.loadPropertiesFile(args[0]);

		//Start Mulicast Data Backup Listener
		MDBListener mdbl= new MDBListener();
		Thread t = new Thread(mdbl);
		t.start();
		
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

		// Multicast Data Channel for BACKUP
		//mdb_collector = new NodeCollector(mdb_ip, mdb_port);

		// Multicast Data Channel for RESTORE
		//mdr_collector = new NodeCollector(mdr_ip, mdr_port);

		// Shell Interpreter
		shell.getShell();

		// TODO: Implementar um listner para ler as respostas dos protocolos
	}

	public static HashMap<String, StoredChunk> getDB() {
		return database;
	}

	public static void saveChunkToDB(String filePathName, String senderID, String fileID, int chunkNo) {
		StoredChunk chunk = new StoredChunk(senderID, fileID, chunkNo);
		database.put(filePathName, chunk);
	}

	public static StoredChunk getStoredChunk(String filePathName) {
		return database.get(filePathName);
	}
}
