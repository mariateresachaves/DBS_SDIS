import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Client {
	public static void main(String[] args) {
		if (args.length < 4) {
			System.err.println("[-] Wrong number of arguments");
			System.exit(1);
		}

		String host_name = args[0].trim();
		int port_number = Integer.parseInt(args[1]);

		String oper = args[2].trim();
		String[] opnd = new String[args.length - 3];

		System.arraycopy(args, 3, opnd, 0, args.length - 3);

		// Plate format verification
		if (!args[3].matches("[A-Za-z0-9]{2}-[A-Za-z0-9]{2}-[A-Za-z0-9]{2}")) {
			System.err.println("[-] Wrong plate format");
			System.exit(3);
		}

		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress address = InetAddress.getByName(host_name);
			byte[] msg = new byte[256];

			if (oper.equalsIgnoreCase("REGISTER"))
				register(opnd, msg);

			else if (oper.equalsIgnoreCase("LOOKUP"))
				lookup(opnd, msg);

			// Send command to the server
			DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port_number);
			socket.send(packet);

			// Receive command response
			byte[] buf = new byte[256];
			DatagramPacket packet_received = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());

			// Socket timeout
			socket.setSoTimeout(3000); // 3 seconds timeout

			try {
				socket.receive(packet_received);
			} catch (SocketTimeoutException ex) {
				System.err.println("[-] No response given by server");
				socket.close();
				System.exit(8);
			}

			String response = new String(packet_received.getData());
			response_msg(response.trim(), oper);

			socket.close();

		} catch (SocketException ex) {
			System.err.println("[-] Fail to create socket");
			System.exit(5);
		} catch (UnknownHostException ex) {
			System.err.println("[-] Fail to create destination address");
			System.exit(6);
		} catch (IOException ex) {
			System.err.println("[-] Fail to send the packet");
			System.exit(7);
		}
	}

	public static void register(String[] opnd, byte[] msg) {
		String cmd = null;

		// Number of arguments verification
		if (opnd.length != 2) {
			System.err.println("[-] Wrong number of arguments for REGISTER operation");
			System.exit(2);
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
			System.exit(4);
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

}