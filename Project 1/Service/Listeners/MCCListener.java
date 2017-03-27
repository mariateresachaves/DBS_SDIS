package Service.Listeners;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import Utils.Util;
import Utils.Util.ErrorCode;

public class MCCListener implements Runnable {

	private PacketCollector collectedMessages;
	private String channelIP;
	private String channelport;

	private MulticastSocket mcast_socket;

	private long ttl;
	private long start_time;

	public MCCListener() {
		// Variables assingment
		channelIP = Util.getProperties().getProperty("MC_IP", "224.13.3.1");
		channelport = Util.getProperties().getProperty("MC_PORT", "9176");

		collectedMessages = new PacketCollector();

		start_time = System.currentTimeMillis();
		ttl = Integer.parseInt(Util.getProperties().getProperty("TTL", "1000"));

		/**
		 * Create threads to clear old messages in queuueueueueu
		 */
		Timer t = new Timer();
		TimerTask clearMessagesinQueu = new TimerTask() {
			@Override
			public void run() {
				synchronized (collectedMessages) {
					collectedMessages.deleteOlderThan(ttl);
				}
			}
		};
		t.scheduleAtFixedRate(clearMessagesinQueu, 0, 1000);

	}

	public MCCListener(int time) {
		// Variables assingment
		channelIP = Util.getProperties().getProperty("MC_IP", "224.13.3.1");
		channelport = Util.getProperties().getProperty("MC_PORT", "9176");

		start_time = System.currentTimeMillis();
		ttl = time;
	}

	public void run() {
		Util.getLogger().log(Level.INFO,
				"Starting Multicast Control Channel Listener");

		try {
			mcast_socket = new MulticastSocket(
					Integer.parseInt(this.channelport));
			mcast_socket.joinGroup(InetAddress.getByName(this.channelIP));
		} catch (Exception e) {
			Util.getLogger().log(Level.SEVERE,
					"Error creating Listener for multicast Restore Channel");
			System.exit(ErrorCode.ERR_CREATELISTMCC.ordinal());
		}

		recieveMessage();
	}

	public void recieveMessage() {
		byte[] buf = new byte[4096];
		DatagramPacket packet_received = new DatagramPacket(buf, buf.length);

		// (System.currentTimeMillis() - start_time) -> elapsed time
		while (true) {
			try {
				mcast_socket.receive(packet_received);
			} catch (IOException e) {
				Util.getLogger().log(Level.SEVERE,
						"Error Recieving packet on control channel");
				System.exit(ErrorCode.ERR_MCC_PACKET.ordinal());
			}

			String response = new String(packet_received.getData());
			String protocolMessage = processProtocol(response);

			synchronized (collectedMessages) {
				collectedMessages.add(new DatedMessage(response, System
						.currentTimeMillis()));
			}

			selectProtocol(protocolMessage);
		}

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
		// Go to the database and check for that chunk

		// if the chunk is present then send the chunk to other channel
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
