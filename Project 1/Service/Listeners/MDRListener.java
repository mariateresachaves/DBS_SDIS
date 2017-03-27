package Service.Listeners;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Formatter;
import java.util.logging.Level;

import Utils.Util;
import Utils.Util.ErrorCode;

public class MDRListener implements Runnable {

	private String recovertLocation;
	private String channelIP;
	private String channelport;

	public MDRListener() {
		channelIP = Util.getProperties().getProperty("Recovery", "./recovery");
		channelIP = Util.getProperties().getProperty("MC_IP", "224.13.3.3");
		channelport = Util.getProperties().getProperty("MC_PORT", "9178");
	}

	public void run() {
		Util.getLogger().log(Level.INFO, "Starting Multicast Data Recovery Channel Listener");
		// Set of variables
		MulticastSocket mcast_socket = null;

		try {
			mcast_socket = new MulticastSocket(Integer.parseInt(this.channelport));
			mcast_socket.joinGroup(InetAddress.getByName(this.channelIP));
		} catch (Exception e) {
			Util.getLogger().log(Level.SEVERE, "Error creating Listener for multicast Recovery Channel");
			System.exit(ErrorCode.ERR_CREATELISTMDR.ordinal());
		}

		recieveMessage(mcast_socket);
	}

	private void recieveMessage(MulticastSocket sck) {
		byte[] buf = new byte[4096];
		DatagramPacket packet_received = new DatagramPacket(buf, buf.length);

		try {
			while (true) {

				sck.receive(packet_received);

				String response = new String(packet_received.getData());
				String protocolMessage = processProtocol(response);

				selectProtocol(protocolMessage);

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
		if (folder.isDirectory() && folder.canWrite()) {
			try {
				Formatter f = new Formatter(new File(folder.getPath() + "/" + protocolMessage.split(" ")[3]));
				String[] processedMessage = protocolMessage.split(" ");
				f.format("%s %s %s \r\n\r\n%s", processedMessage[2], processedMessage[3], processedMessage[4],
						processedMessage[5]);
				f.flush();
				f.close();
			} catch (FileNotFoundException e) {
				Util.getLogger().log(Level.WARNING, "Error saving recovery message");
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

}
