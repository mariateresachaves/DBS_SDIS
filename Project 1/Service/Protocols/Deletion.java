package Service.Protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.logging.Level;

import Service.Peer;
import Service.StoredChunk;
import Utils.Util;

public class Deletion {
	private static StoredChunk chunk_info;
	private static String version;

	private static String hostname;
	private static int port;
	private static InetAddress address;

	public Deletion(String filePathName) {
		chunk_info = Peer.getStoredChunk(filePathName).get(0);
		version = "1.0";
	}

	public void send_delete() throws IOException {
		Util.getLogger().log(Level.INFO, "Sending DELETE to MC Channel");

		// MDB Channel
		hostname = Util.getProperties().getProperty("MC_IP");
		port = Integer.parseInt(Util.getProperties().getProperty("MC_PORT"));
		address = InetAddress.getByName(hostname);

		// Create message to send
		String tmp_msg = String.format("DELETE %s %s %s \r\n\r\n", version, chunk_info.getSenderID(),
				chunk_info.getFileID());

		byte[] msg = tmp_msg.getBytes();

		// Socket to send the message
		DatagramSocket socket = new DatagramSocket();

		DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port);
		socket.send(packet);

		socket.close();
	}

}
