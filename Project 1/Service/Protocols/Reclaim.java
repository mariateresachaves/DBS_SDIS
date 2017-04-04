package Service.Protocols;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;

import Service.StoredChunk;
import Utils.Util;

/*
 * The algorithm for managing the disk space reserved for the backup service
 * is not specified. Each implementation can use its own. However, when a
 * peer deletes a copy of a chunk it has backed up, it shall send to the MC
 * channel the following message:
 * 
 * REMOVED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
 */

public class Reclaim {

	private static StoredChunk chunk_info;
	private static String version;

	private static String hostname;
	private static int port;
	private static InetAddress address;

	public Reclaim() {
		version = "1.0";
	}

	public void send_removed() throws IOException {
		Util.getLogger().log(Level.INFO, "Sending REMOVED to MC Channel");

		// TODO: Which chunk is going to be removed?

		// Create message to send
		/*
		 * String tmp_msg = String.format("REMOVED %s %s %s %d %d \r\n%s",
		 * version, chunk.getSenderID(), chunk.getFileID(), chunk.getChunkNo(),
		 * chunk.getReplicationDegree(), chunk.getBodyData());
		 * 
		 * byte[] msg = tmp_msg.getBytes();
		 * 
		 * // Socket to send the message DatagramSocket socket = new
		 * DatagramSocket();
		 * 
		 * // MDB Channel hostname = Util.getProperties().getProperty("MDB_IP");
		 * port =
		 * Integer.parseInt(Util.getProperties().getProperty("MDB_PORT"));
		 * address = InetAddress.getByName(hostname);
		 * 
		 * DatagramPacket packet = new DatagramPacket(msg, msg.length, address,
		 * port);
		 * 
		 * socket.send(packet);
		 * 
		 * socket.close();
		 */
	}
}
