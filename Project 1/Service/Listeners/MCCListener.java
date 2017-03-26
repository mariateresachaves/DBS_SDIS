package Service.Listeners;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;

import Utils.Util;

public class MCCListener implements Runnable {

	private String channelIP;
	private String channelport;

	MulticastSocket mcast_socket;

	public MCCListener() {
		// Variables assingment
		channelIP = Util.getProperties().getProperty("MC_IP", "224.13.3.1");
		channelport = Util.getProperties().getProperty("MC_PORT", "9176");
	}

	public void run() {
		Util.getLogger().log(Level.INFO, "Starting Multicast Control Channel Listener");

		try {
			mcast_socket = new MulticastSocket(Integer.parseInt(this.channelport));
			mcast_socket.joinGroup(InetAddress.getByName(this.channelIP));
		} catch (Exception e) {
			Util.getLogger().log(Level.SEVERE, "Error creating Listener for multicast Restore Channel");
			System.exit(Util.ERR_CREATELISTMCC);
		}

		// recieveMessage(mcast_socket);
	}

	public DatagramPacket recieveMessage(MulticastSocket sck) {
		byte[] buf = new byte[4096];
		DatagramPacket packet_received = new DatagramPacket(buf, buf.length);

		try {
			while (true) {
				sck.receive(packet_received);

				// String response = new String(packet_received.getData());
				// String protocolMessage = processProtocol(response);

				// selectProtocol(protocolMessage);
			}
		} catch (Exception e) {
			Util.getLogger().log(Level.WARNING, "Error Recieving packet, Error Message: ");
			e.printStackTrace();
		}

		return packet_received;
	}

	private void selectProtocol(String protocolMessage) {
		switch (protocolMessage.split(" ")[0]) {

		case "GETCHUNK":
			getChunkProtocol();
			break;
		case "DELETE":
			deleteProtocol();
			break;
		case "REMOVED":
			removedProtocol();
			break;
		case "STORED":
			storedProtocol();
			break;
		}
	}

	private void storedProtocol() {

	}

	private void removedProtocol() {
		// TODO Auto-generated method stub
	}

	private void deleteProtocol() {
		// TODO Auto-generated method stub
	}

	private void getChunkProtocol() {
		// TODO Auto-generated method stub
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

	public MulticastSocket getMCastSocket() {
		return mcast_socket;
	}

}
