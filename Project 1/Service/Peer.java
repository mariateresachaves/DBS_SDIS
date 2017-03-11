package Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import Utils.Utils;

public class Peer {

	private static Server mc_server;
	private static int mc_rate;
	
	private static NodeCollector mc_collector;
	private static NodeCollector mdb_collector;
	private static NodeCollector mdr_collector;
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		// Load properties file
		//Utils.Utils.loadPropertiesFile(args[1]);
		
		// Join multicast groups
		//String mc_ip = Utils.Utils.getPropertie().getProperty("MC_IP");
		//String mdb_ip = Utils.Utils.getPropertie().getProperty("MDB_IP");
		//String mdr_ip = Utils.Utils.getPropertie().getProperty("MDR_IP");
		
		if (args.length < 6) {
			System.out.println("Usage: Peer <MC_IP> <MC_Port> <MDB_IP> <MDB_Port> <MDR_IP> <MDR_Port>");
			Utils.getLogger().log(Level.SEVERE, "Invalid arguments at the start of application");
			System.exit(Utils.ERR_WRONG_ARGS);
		}
		
		mc_rate = Integer.parseInt(Utils.getPropertie().getProperty("MC_RATE"));
		
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
