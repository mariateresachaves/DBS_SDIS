package Service;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Client {
	/**
     * Socket-related variables
     */
	private static DatagramPacket packet;
	private static DatagramSocket socket;
	private static String address;
	private static int port;
	private static InetAddress unicast_address;
	private static MulticastSocket mcast_socket;
	private static InetAddress mcast_address;
	private static Object lock = new Object();
	
	
	public static void main(String[] args) {
		// Arguments verification
		check_args(args);

		String addr = args[0].trim();
		int mcast_port = Integer.parseInt(args[1]);
		String oper = args[2].trim();
		String[] opnd = new String[args.length - 3];

		System.arraycopy(args, 3, opnd, 0, args.length - 3);

		// Plate format verification
		check_plate_format(args[3]);

		try {
			byte[] msg = new byte[256];

			socket = new DatagramSocket();
			mcast_socket = new MulticastSocket(mcast_port);
			mcast_address = InetAddress.getByName(addr);

			// Join multicast channel
			mcast_socket.joinGroup(mcast_address);

			Timer t = new Timer();
			TimerTask adv_network = new TimerTask() {

				@Override
				public void run() {

					// Receive command response
					byte[] buf = new byte[256];
					DatagramPacket packet_received = new DatagramPacket(buf, buf.length);

					try {
						while (true) {
							// Time-To-Live
							mcast_socket.setTimeToLive(1);
							mcast_socket.receive(packet_received);

							String response = new String(packet_received.getData());
							System.out.println("msg: " + response);
						}

					} catch (IOException e) {
						System.err.println("[-] Fail to receive the packet from multicast");
						System.exit(Utils.ERR_RECEIVE);
					}

					// Parse received message
					String response = new String(packet_received.getData());
					String[] response_data = response.split(":");

					synchronized (lock) {
						address = response_data[1].trim();
						port = Integer.parseInt(response_data[2].trim());
					}
				}
			};

			t.schedule(adv_network, 0);

			if (address != null) {
				unicast_address = InetAddress.getByName(address);

				if (oper.equalsIgnoreCase("REGISTER"))
					register(opnd, msg);

				else if (oper.equalsIgnoreCase("LOOKUP"))
					lookup(opnd, msg);

				// Send command to the server
				packet = new DatagramPacket(msg, msg.length, unicast_address, port);
				socket.send(packet);

				// Socket timeout
				socket.setSoTimeout(3000); // 3 seconds timeout
			}

			byte[] buf = new byte[256];
			DatagramPacket packet_received = new DatagramPacket(buf, buf.length, unicast_address, port);

			String response = new String(packet_received.getData());
			response_msg(response.trim(), oper);

			socket.close();

		}

		catch (UnknownHostException ex) {
			System.err.println("[-] Fail to create destination address");
			System.exit(Utils.ERR_DEST_ADDR);
		}

		catch (IOException ex) {
			System.err.println("[-] Fail to send the packet");
			System.exit(Utils.ERR_SEND);
		}
	}

	public static void register(String[] opnd, byte[] msg) {
		String cmd = null;

		// Number of arguments verification
		if (opnd.length != 2) {
			System.err.println("[-] Wrong number of arguments for REGISTER operation");
			System.exit(Utils.ERR_REGISTER_WRONG_ARGS);
		}
		// Command construction
		else {
			cmd = String.format("REGISTER:%s:%s", opnd[0], opnd[1]);
			System.arraycopy(cmd.getBytes(), 0, msg, 0, cmd.length());
		}
	}

	public static void lookup(String[] opnd, byte[] msg) {
		String cmd = null;

		// Number of arguments verification
		if (opnd.length != 1) {
			System.err.println("[-] Wrong number of arguments for LOOKUP operation");
			System.exit(Utils.ERR_LOOKUP_WRONG_ARGS);
		}
		// Command construction
		else {
			cmd = String.format("LOOKUP:%s", opnd[0]);
			System.arraycopy(cmd.getBytes(), 0, msg, 0, cmd.length());
		}
	}

	public static void response_msg(String response, String oper) {
		if (oper.equalsIgnoreCase("REGISTER")) {
			switch (response) {
			case "0":
				System.out.println("[+] Plate registered successfuly");
				break;

			case "-1":
				System.out.println("[-] Plate already registered");
				break;

			default:
				break;
			}
		}

		else if (oper.equalsIgnoreCase("LOOKUP")) {
			switch (response) {
			case "NOT_FOUND":
				System.out.println("[-] Plate not registered");
				break;

			default:
				System.out.println("[+] Plate owner: " + response);
				break;
			}
		}
	}

	public static void check_args(String[] args) {
		if (args.length < 4) {
			System.err.println("[-] Wrong number of arguments");
			System.exit(Utils.ERR_NUM_ARGS);
		}
	}

	public static void check_plate_format(String plate) {
		if (!plate.matches("[A-Za-z0-9]{2}-[A-Za-z0-9]{2}-[A-Za-z0-9]{2}")) {
			System.err.println("[-] Wrong plate format");
			System.exit(Utils.ERR_PLATE_NUM);
		}
	}
}