package Service.Protocols;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import Utils.Util;

public class Backup {

	// PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>

	private static List<Chunk> chunks;
	private static String version;

	public Backup(String filePathName, String replicationDegree) throws Exception {

		version = "1.0"; // TODO: mudar para poder ser generico
		File f = new File(filePathName);
		int chunkSize = Integer.parseInt(Util.getProperties().getProperty("CHUNK_SIZE"));

		// Split file into chunks
		ChunkController controller = new ChunkController();
		chunks = controller.breakIntoChunks(f, chunkSize, Integer.parseInt(replicationDegree));
	}

	public static void send_message(String msg_name) {

		switch (msg_name.toUpperCase()) {
		case "PUTCHUNK":
			// Backs up each chunk independently

			// TODO: Initiator-peer sends to the MDB a message:
			// PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
			break;

		case "STORED":
			/*
			 * Maybe on Listner!
			 * 
			 * TODO: A peer that stores the chunk upon receiving the PUTCHUNK
			 * message, should reply by sending on the multicast control channel
			 * (MC) a confirmation message with the following format:
			 * 
			 * STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
			 */
			break;

		default:
			break;
		}
	}

	public static List<Chunk> get_chunks() {
		return chunks;
	}
}
