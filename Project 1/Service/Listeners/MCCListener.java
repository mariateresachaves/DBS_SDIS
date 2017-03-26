package Service.Listeners;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import Utils.Util;

public class MCCListener implements Runnable {

	private String channelIP;
	private String channelport;

	private MulticastSocket mcast_socket;

	private long ttl;
	private long start_time;

	public MCCListener(int time) {
		// Variables assingment
		channelIP = Util.getProperties().getProperty("MC_IP", "224.13.3.1");
		channelport = Util.getProperties().getProperty("MC_PORT", "9176");

		start_time = System.currentTimeMillis();
		ttl = time;
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

		recieveMessage();
	}

	public void recieveMessage() {
		byte[] buf = new byte[4096];
		DatagramPacket packet_received = new DatagramPacket(buf, buf.length);

		// (System.currentTimeMillis() - start_time) -> elapsed time
		while ((System.currentTimeMillis() - start_time) <= ttl) {
			try {
				mcast_socket.setSoTimeout((int) (ttl - (System.currentTimeMillis() - start_time)));
				mcast_socket.receive(packet_received);
			} catch (SocketTimeoutException e1) {
				Util.getLogger().log(Level.WARNING, "Error Recieving packet");
			} catch (IOException e) {
				Util.getLogger().log(Level.SEVERE, "Error Setting Socket Timeout for MCC Listener");
				System.exit(Util.ERR_SOCKET_TIMEOUT);
			}

			String response = new String(packet_received.getData());
			String protocolMessage = processProtocol(response);

			selectProtocol(protocolMessage);
		}

		Thread.currentThread().interrupt();
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

}
