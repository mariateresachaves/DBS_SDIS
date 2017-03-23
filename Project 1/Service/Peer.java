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
	private static String mdb_ip;
	private static String mdr_ip;

	private static NodeCollector mc_collector;
	private static NodeCollector mdb_collector;
	private static NodeCollector mdr_collector;
	private static ShellInterpreter shell= new ShellInterpreter();

	public static void main(String[] args) throws FileNotFoundException, IOException {

		//Para teste
		//File props= new File("properties.txt");
		//Utils.loadPropertiesFile(props);
		shell.getShell();
		
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
		mdb_ip = Utils.getProperties().getProperty("MDB_IP");
		mdr_ip = Utils.getProperties().getProperty("MDR_IP");
		

		// Multicast Control Channel
		String mc_addr = args[0].trim();
		int mc_port = Integer.parseInt(args[1]);

		mc_server = new Server(mc_addr, mc_port, mc_rate);
		mc_server.createMenance();

		mc_collector = new NodeCollector(mc_addr, mc_port);

		// Multicast Data Channel for BACKUP
		String mdb_addr = args[2].trim();
		int mdb_port = Integer.parseInt(args[3]);

		mdb_collector = new NodeCollector(mdb_addr, mdb_port);

		// Multicast Data Channel for RESTORE
		String mdr_addr = args[4].trim();
		int mdr_port = Integer.parseInt(args[5]);

		mdr_collector = new NodeCollector(mdr_addr, mdr_port);
		
		

	}

}
