package Service.Protocols;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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

		version = "1.0";
		File f = new File(filePathName);
		int chunkSize = Integer.parseInt(Util.getProperties().getProperty("CHUNK_SIZE"));

		// Split file into chunks
		ChunkController controller = new ChunkController();
		chunks = controller.breakIntoChunks(f, chunkSize, Integer.parseInt(replicationDegree));
	}

	public void send_message(String msg_name, Chunk chunk) throws IOException {
		Util.getLogger().log(Level.INFO, "Sending Message Backup Protocol");

		// Create message to send
		byte[] msg = make_message(msg_name.toUpperCase(), chunk);

		// Socket to send the message
		DatagramSocket socket = new DatagramSocket();

		switch (msg_name.toUpperCase()) {
		case "PUTCHUNK":
			// MDB Channel
			hostname = Util.getProperties().getProperty("MDB_IP");
			port = Integer.parseInt(Util.getProperties().getProperty("MDB_PORT"));
			address = InetAddress.getByName(hostname);
			break;

		case "STORED":
			// MC Channel
			hostname = Util.getProperties().getProperty("MC_IP");
			port = Integer.parseInt(Util.getProperties().getProperty("MC_PORT"));
			address = InetAddress.getByName(hostname);
			break;

		default:
			break;
		}

		DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port);
		socket.send(packet);
		
		socket.close();
	}

	public List<Chunk> get_chunks() {
		return chunks;
	}

	public static byte[] make_message(String name, Chunk chunk) {
		Util.getLogger().log(Level.INFO, "Creating Message Backup Protocol");

		byte[] msg = null;
		String tmp_msg;

		switch (name) {
		// PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
		case "PUTCHUNK":
			tmp_msg = String.format("PUTCHUNK %s %s %s %d %d 0xD0xA%s", version, chunk.getSenderID(), chunk.getFileID(),
					chunk.getChunkNo(), chunk.getReplicationDegree(), chunk.getBodyData());

			msg = tmp_msg.getBytes();
			break;

		// STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
		case "STORED":
			tmp_msg = String.format("STORED %s %s %s %d %d 0xD0xA", version, chunk.getSenderID(), chunk.getFileID(),
					chunk.getChunkNo());

			msg = tmp_msg.getBytes();
			break;

		default:
			break;
		}

		return msg;
	}
}
