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
		Util.getLogger().log(Level.INFO, "Breaking File Into Chunks");

		version = "1.0";
		File f = new File(filePathName);
		int chunkSize = Integer.parseInt(Util.getProperties().getProperty("CHUNK_SIZE"));

		// Split file into chunks
		ChunkController controller = new ChunkController();
		chunks = controller.breakIntoChunks(f, chunkSize, Integer.parseInt(replicationDegree));
	}

	public DatagramPacket make_packet(Chunk chunk) throws UnknownHostException, SocketException {
		Util.getLogger().log(Level.INFO, "Sending PUTCHUNK to MDB Channel");

		
		//Loading Body
		String bodymsg="";
		for(byte x: chunk.getBodyData()){
			bodymsg+=String.format("%02x", x);
		}
			
		// Create message to send
		String tmp_msg = String.format("PUTCHUNK %s %s %s %d %d \r\n%s", version, chunk.getSenderID(),
				chunk.getFileID(), chunk.getChunkNo(), chunk.getReplicationDegree(), bodymsg);

		byte[] msg = tmp_msg.getBytes();

		// MDB Channel
		hostname = Util.getProperties().getProperty("MDB_IP");
		port = Integer.parseInt(Util.getProperties().getProperty("MDB_PORT"));
		address = InetAddress.getByName(hostname);

		DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port);
		
		return packet;
	}
	
	public void send_putchunk(DatagramPacket packet) throws IOException {
		Util.getLogger().log(Level.INFO, "Sending PUTCHUNK to MDB Channel");
		
		// Socket to send the message
		DatagramSocket socket = new DatagramSocket();

		socket.send(packet);

		socket.close();
	}

	public List<Chunk> get_chunks() {
		return chunks;
	}

}
