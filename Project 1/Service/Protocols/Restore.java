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

public class Restore {

	private static ArrayList<StoredChunk> chunks_info;
	private static String version;

	private static String hostname;
	private static int port;
	private static InetAddress address;

	public Restore(String filePathName) {
		chunks_info = Peer.getStoredChunk(filePathName);
		version = "1.0";
	}

	public void send_getchunk() throws IOException {
		Util.getLogger().log(Level.INFO, "Sending GETCHUNK to MC Channel");

		String tmp_msg = null;
		byte[] msg;

		// Socket to send the message
		DatagramSocket socket = new DatagramSocket();

		// MDB Channel
		hostname = Util.getProperties().getProperty("MC_IP");
		port = Integer.parseInt(Util.getProperties().getProperty("MC_PORT"));
		address = InetAddress.getByName(hostname);

		// Create message to send
		for (StoredChunk chunk_info : chunks_info) {
			tmp_msg = String.format("GETCHUNK %s %s %s %d \r\n\r\n", version, chunk_info.getSenderID(),
					chunk_info.getFileID(), chunk_info.getChunkNo());

			msg = tmp_msg.getBytes();

			DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port);
			socket.send(packet);
		}

		socket.close();
	}

}
