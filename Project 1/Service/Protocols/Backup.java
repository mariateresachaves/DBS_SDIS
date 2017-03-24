package Service.Protocols;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import Utils.Util;

public class Backup {

	// PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg>
	// <CRLF><CRLF><Body>

	private static String version;
	private static String senderID;
	private static String fileID;
	private static int chunkNo;
	private static int replicationDeg;
	private static String body;

	public Backup(String filePathName, String replicationDegree) throws Exception {

		version = "1.0"; // TODO: mudar para poder ser generico
		File f = new File(filePathName);
		int chunkSize = Integer.parseInt(Util.getProperties().getProperty("CHUNK_SIZE"));

		// Split file into chunks
		ChunkController controller = new ChunkController();
		List<Chunk> chunks = controller.breakIntoChunks(f, chunkSize, Integer.parseInt(replicationDegree));

		for (Chunk chunk : chunks) {
			senderID = chunk.getSenderID();
			fileID = chunk.getFileID();
			chunkNo = chunk.getChunkNo();
			replicationDeg = chunk.getReplicationDegree();
			body = chunk.getBodyData();
			
			// TODO: Initiator-peer sends to the MDB a message:
			// PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>


			/* TODO:
			 * A peer that stores the chunk upon receiving the PUTCHUNK message,
			 * should reply by sending on the multicast control channel (MC) a
			 * confirmation message with the following format:
			 * 
			 * STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
			 */
		}
	}

}
