package Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import Utils.Util;
import java.util.ArrayList;

public class NodeCollector {

	/**
	 * Correlates a node -> (Service,lastSeen)
	 */
	private Map<String, Map<String, String>> database;

	/**
	 * RateThreshold will control the timeout of nodes
	 */
	private static int rateThreshold = 10000;
	private static int trashCollectorTime = 5000;
	private static MulticastSocket mcast_socket;
	private static InetAddress mcast_address;

	public NodeCollector(String address, int port, int trashRate, int timeoutThreshold) {
		this(address, port);
		rateThreshold = timeoutThreshold;
		trashCollectorTime = trashRate;
	}

	/**
	 * Default Constructor
	 *
	 * @param address
	 *            - Multicast Address to listen to
	 * @param port
	 *            - Port from multicast address to listen to
	 */
	public NodeCollector(String address, int port) {
		database = new HashMap<>();

		try {
			// Prepare the socket
			mcast_socket = new MulticastSocket(port);
			mcast_address = InetAddress.getByName(address);

			// Join multicast channel
			mcast_socket.joinGroup(mcast_address);

		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "[-] - Error reserving the Log Port");
		}

		Timer t = new Timer();
		Timer tGarbage = new Timer();
		TimerTask rcvData = new TimerTask() {
			@Override
			public void run() {
				collectPackage();

			}
		};
		TimerTask remoNodes = new TimerTask() {
			@Override
			public void run() {
				removeTimeOutNodes();
			}
		};
		t.schedule(rcvData, 0);
		tGarbage.schedule(remoNodes, 0, trashCollectorTime);
	}

	private int collectPackage() {
		// Receive command response
		byte[] buf = new byte[256];
		DatagramPacket packet_received = new DatagramPacket(buf, buf.length);

		try {
			while (true) {
				// Time-To-Live
				mcast_socket.setTimeToLive(1);
				mcast_socket.receive(packet_received);

				String response = new String(packet_received.getData());
				String time = System.currentTimeMillis() + "";
				String[] responseData = response.split(":");

				if (response.startsWith("Multicast Message")) {
					synchronized (database) {
						if (database.containsKey(responseData[1])) {
							if (database.get(responseData[1]).get(responseData[2]) != null) {
								// UPDATE
								database.get(responseData[1]).remove(responseData[2]);
								database.get(responseData[1]).put(responseData[2], time);
							} else {
								database.get(responseData[1]).put(responseData[2], time);
							}
						} else {
							HashMap<String, String> values = new HashMap<String, String>();
							values.put(responseData[2], time);
							database.put(responseData[1], values);
						}
					}
				}
			}
		} catch (IOException e) {
			System.err.println("[-] Fail to receive the packet from multicast");
			System.exit(Util.ERR_RECEIVE);
		}
		return 0;
	}

	private void removeTimeOutNodes() {
		synchronized (database) {
			for (Map.Entry<String, Map<String, String>> entry : database.entrySet()) {
				String key = entry.getKey();
				Map<String, String> value = entry.getValue();
				for (Map.Entry<String, String> entry1 : value.entrySet()) {
					String key1 = entry1.getKey();
					String value1 = entry1.getValue();

					long ti = System.currentTimeMillis();
					long tf = Long.parseLong(value1);
					if (Math.abs(ti - tf) > NodeCollector.rateThreshold) {
						database.get(key).remove(key1);
						// Check if there are no more subnodes
						if (database.get(key).isEmpty()) {
							database.remove(key);
							return;
						}
						return;
					}
				}
			}
		}
	}

	// <IP:Port>
	public ArrayList<String> getNodes() {
		ArrayList<String> ret = new ArrayList<String>();
		synchronized (database) {
			for (Map.Entry<String, Map<String, String>> entry : database.entrySet()) {
				String key = entry.getKey();
				Map<String, String> value = entry.getValue();
				for (Map.Entry<String, String> entry1 : value.entrySet()) {
					String key1 = entry1.getKey();
					ret.add(String.format("%s-%s", key, key1));
				}
			}
		}
		return ret;
	}

}
