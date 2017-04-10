package Service.Protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;

import Utils.Util;

public class Reclaim {
	private String senderId;
	private String fileId;
	private int chunkNo;
	private static String version;

	private static String hostname;
	private static int port;
	private static InetAddress address;

	public Reclaim(String senderId, String fileId, int chunkNo) {
		this.senderId = senderId;
		this.fileId = fileId;
		this.chunkNo = chunkNo;
		version = "1.0";
	}

	public void send_removed() throws IOException {
		Util.getLogger().log(Level.INFO, "Sending REMOVED to MC Channel\n");

		// MDB Channel
		hostname = Util.getProperties().getProperty("MC_IP");
		port = Integer.parseInt(Util.getProperties().getProperty("MC_PORT"));
		address = InetAddress.getByName(hostname);

		// Create message to send
		// REMOVED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
		String tmp_msg = String.format("REMOVED %s %s %s %s \r\n\r\n", version,
				senderId, fileId, chunkNo);

		byte[] msg = tmp_msg.getBytes();

		// Socket to send the message
		DatagramSocket socket = new DatagramSocket();

		DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port);
		socket.send(packet);

		socket.close();
	}
}
