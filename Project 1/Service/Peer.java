package Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import Utils.Utils;

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

	public static void main(String[] args) throws FileNotFoundException, IOException {
		if (args.length != 1) {
			System.out.println("Usage: Peer <PropertiesFile>");
			Utils.getLogger().log(Level.SEVERE, "Invalid arguments at the start of application");
			System.exit(Utils.ERR_WRONG_ARGS);
		}

		// Load properties file
		Utils.loadPropertiesFile(args[0]);

		// Get multicast channels properties
		mc_rate = Integer.parseInt(Utils.getProperties().getProperty("MC_RATE"));

		mc_ip = Utils.getProperties().getProperty("MC_IP");
		mc_port = Integer.parseInt(Utils.getProperties().getProperty("MC_PORT"));

		mdb_ip = Utils.getProperties().getProperty("MDB_IP");
		mdb_port = Integer.parseInt(Utils.getProperties().getProperty("MDB_PORT"));

		mdr_ip = Utils.getProperties().getProperty("MDR_IP");
		mdr_port = Integer.parseInt(Utils.getProperties().getProperty("MDR_PORT"));

		// Multicast Control Channel
		mc_server = new Server(mc_ip, mc_port, mc_rate);
		mc_server.createMenance();

		mc_collector = new NodeCollector(mc_ip, mc_port);

		// Multicast Data Channel for BACKUP
		mdb_collector = new NodeCollector(mdb_ip, mdb_port);

		// Multicast Data Channel for RESTORE
		mdr_collector = new NodeCollector(mdr_ip, mdr_port);

		// Shell Interpreter
		shell.getShell();

		// TODO: Implementar um listner para ler as respostas dos protocolos
	}

}
