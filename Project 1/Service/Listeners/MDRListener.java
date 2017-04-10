package Service.Listeners;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.logging.Level;

import Utils.Util;
import Utils.Util.ErrorCode;

public class MDRListener implements Runnable {

	private String recovertLocation;
	private String channelIP;
	private String channelport;
	private static ArrayList<String> packetsForRecovery = new ArrayList<>();

	public MDRListener() {
		recovertLocation = Util.getProperties().getProperty("Recovery", "./recovery");
		channelIP = Util.getProperties().getProperty("MDR_IP", "224.13.3.3");
		channelport = Util.getProperties().getProperty("MDR_PORT", "9178");
	}

	public void run() {
		Util.getLogger().log(Level.INFO, "Starting Multicast Data Recovery Channel Listener\n");
		// Set of variables
		MulticastSocket mcast_socket = null;

		try {
			mcast_socket = new MulticastSocket(Integer.parseInt(this.channelport));
			mcast_socket.joinGroup(InetAddress.getByName(this.channelIP));
		} catch (Exception e) {
			Util.getLogger().log(Level.SEVERE, "Error creating Listener for multicast Recovery Channel\n");
			System.exit(ErrorCode.ERR_CREATELISTMDR.ordinal());
		}

		recieveMessage(mcast_socket);
	}

	private void recieveMessage(MulticastSocket sck) {

		try {
			while (true) {
				byte[] buf = new byte[4096];
				DatagramPacket packet_received = new DatagramPacket(buf, buf.length);
				sck.receive(packet_received);

				String response = new String(packet_received.getData());
				String protocolMessage = processProtocol(response);

				packetsForRecovery.add(protocolMessage);

				selectProtocol(protocolMessage);
				// System.out.println("RECIEVED PACKET!");
				// System.out.println(protocolMessage);
			}
		} catch (Exception e) {
			Util.getLogger().log(Level.WARNING, "Error Recieving packet, Error Message: ");
			e.printStackTrace();
		}
	}

	private void selectProtocol(String protocolMessage) {
		switch (protocolMessage.split(" ")[0]) {

		case "CHUNK":
			recoverChunk(protocolMessage);
			break;
		}
	}

	private void recoverChunk(String protocolMessage) {

		File folder = new File(recovertLocation + "/" + protocolMessage.split(" ")[3]);
		if (!folder.exists()) {
			folder.mkdir();
		}
		// System.out.println("FOLDER-> "+ recovertLocation + "/" +
		// protocolMessage.split(" ")[3]);
		folder = new File(recovertLocation + "/" + protocolMessage.split(" ")[3]);
		if (folder.isDirectory() && folder.canWrite()) {
			try {
				Formatter f = new Formatter(new File(folder.getPath() + "/" + protocolMessage.split(" ")[4]));
				String[] processedMessage = protocolMessage.split(" ");
				f.format("%s %s %s \r\n\r\n%s", processedMessage[2], processedMessage[3], processedMessage[4],
						processedMessage[6]);
				f.flush();
				f.close();
			} catch (FileNotFoundException e) {
				Util.getLogger().log(Level.WARNING, "Error saving recovery message\n");
			}
		}
	}

	private String processProtocol(String response) {
		String old = response;

		String processed;
		processed = old.replaceAll("  ", " ");
		if (old.equalsIgnoreCase(processed)) {
			return processed;
		} else {
			return processProtocol(processed);
		}
	}

	public static ArrayList<String> getRestores(String fileID) {
		ArrayList<String> ret = new ArrayList<>();
		ArrayList<String> temp = new ArrayList<String>(packetsForRecovery);

		for (String msg : temp) {

			if (msg.startsWith("CHUNK")) {

				if (msg.split(" ")[3].trim().equalsIgnoreCase(fileID.trim()))
					ret.add(msg);

			}
		}
		return ret;
	}
}