package Service.Protocols;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;

import Utils.Util;

public class Backup {
	private static List<Chunk> chunks;
	private static String version;

	private static String hostname;
	private static int port;
	private static InetAddress address;

	public Backup(String filePathName, String replicationDegree) throws Exception {
		Util.getLogger().log(Level.INFO, "Breaking File Into Chunks Backup Protocol");

		version = "1.0";
		File f = new File(filePathName);
		int chunkSize = Integer.parseInt(Util.getProperties().getProperty("CHUNK_SIZE"));

		// Split file into chunks
		ChunkController controller = new ChunkController();
		chunks = controller.breakIntoChunks(f, chunkSize, Integer.parseInt(replicationDegree));
	}

	public void send_putchunk(Chunk chunk) throws IOException {
		Util.getLogger().log(Level.INFO, "Sending PUTCHUNK to MDB Channel");

		// Create message to send
		String tmp_msg = String.format("PUTCHUNK %s %s %s %d %d 0xD0xA0xD0xA%s", version, chunk.getSenderID(),
				chunk.getFileID(), chunk.getChunkNo(), chunk.getReplicationDegree(), chunk.getBodyData());

		byte[] msg = tmp_msg.getBytes();

		// Socket to send the message
		DatagramSocket socket = new DatagramSocket();

		// MDB Channel
		hostname = Util.getProperties().getProperty("MDB_IP");
		port = Integer.parseInt(Util.getProperties().getProperty("MDB_PORT"));
		address = InetAddress.getByName(hostname);

		DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port);
		socket.send(packet);

		socket.close();
	}

	public List<Chunk> get_chunks() {
		return chunks;
	}
}
