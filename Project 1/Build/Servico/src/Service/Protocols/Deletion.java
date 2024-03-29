package Service.Protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;

import Service.Peer;
import Service.StoredChunk;
import Utils.Util;

public class Deletion {

	private static String chunk_fileID;
	private static String version;

	private static String hostname;
	private static int port;
	private static InetAddress address;

	public Deletion(String filePathName) {
		chunk_fileID = Peer.xmldb.getFileID(filePathName);
		version = "1.0";
	}

	public void send_delete() throws IOException {
		Util.getLogger().log(Level.INFO, "Sending DELETE to MC Channel\n");

		// MDB Channel
		hostname = Util.getProperties().getProperty("MC_IP");
		port = Integer.parseInt(Util.getProperties().getProperty("MC_PORT"));
		address = InetAddress.getByName(hostname);

		// Create message to send
		String tmp_msg = String.format("DELETE %s %s %s \r\n\r\n", version, Util.getProperties().getProperty("SenderID"),
				chunk_fileID);

		byte[] msg = tmp_msg.getBytes();

		// Socket to send the message
		DatagramSocket socket = new DatagramSocket();

		DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port);
		socket.send(packet);

		socket.close();
	}

}
