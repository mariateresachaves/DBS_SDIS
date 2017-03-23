package Service;

import Utils.Utils;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class Server {
	/**
	 * Socket-related variables
	 */
	private static DatagramSocket serverSocket;
	private static InetAddress addr;
	private static String mcast_addr;
	private static int mcast_port;

	/**
	 * Static Variables
	 */
	private static int mcastRate;

	public Server(String mcastAdress, int mcastPort, int rate) {
		mcast_addr = mcastAdress;
		mcast_port = mcastPort;
		mcastRate = rate;

		try {
			addr = InetAddress.getByName(mcast_addr);
			serverSocket = new DatagramSocket();
		} catch (UnknownHostException ex) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
			throwErrorandExit("[-] Error setting multicast Destination", Utils.ERR_SETTING_ADV);

		} catch (SocketException ex) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
			throwErrorandExit("[-] Error setting multicast socket", Utils.ERR_SETTING_SOCK);
		}

	}

	public static void main(String[] args) {

		// For testing porpuses
		Server abc = new Server("192.168.32.182", 1111, 1000);
		abc.createMenance();

	}

	private static void throwErrorandExit(String msg, int error) {
		System.err.println(msg);
		System.exit(error);
	}

	public void createMenance() {
		/**
		 * Create threads
		 */
		Timer t = new Timer();
		TimerTask adv_network = new TimerTask() {
			@Override
			public void run() {
				try {
					String local_addr = InetAddress.getLocalHost().toString().split("/")[1];
					String msg = String.format("Multicast Message:%s:%d", local_addr, mcast_port);
					DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, addr,
							mcast_port);
					serverSocket.send(msgPacket);
					String status = String.format("multicast: <mcast_addr>%s <mcast_port>%d: <srvc_addr>%s", mcast_addr,
							mcast_port, local_addr);
					// System.out.println(status);
				} catch (IOException ex) {
					System.err.println("[-] Error sending multicast Message");
					System.exit(Utils.ERR_SENDING_ADV);

				}
			}
		};
		t.scheduleAtFixedRate(adv_network, 0, mcastRate);
	}
}
