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
	// Chunk(String senderID, String fileID, int chunkNo, String bodyData)

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
		List<Chunk> chunks = controller.breakIntoChunks(f, chunkSize);
		
		for(Chunk chunk : chunks) {
			senderID = chunk.getSenderID();
			fileID = chunk.getFileID();
			chunkNo = chunk.getChunkNo();
			replicationDeg = chunk.getReplicationDegree();
			body = chunk.getBodyData();
			
			// Just a Test
			System.out.println("version - " + version);
			System.out.println("senderID - " + senderID);
			System.out.println("fileID - " + fileID);
			System.out.println("chunkNo - " + chunkNo);
			System.out.println("replicationDeg - " + replicationDeg);
			//System.out.println("body - " + body);
		}
		
		// Just a Test
		System.out.println("File path name: " + filePathName);
		System.out.println("Replication degree: " + replicationDegree);
		
		
		/* ------------ OLD CODE ---------- */
		
		/*String s_message = new String(message);
		String[] header = s_message.split(" ");

		version = header[1];
		senderId = header[2];
		fileId = header[3];
		chunkNo = Integer.parseInt(header[4]);
		replicationDeg = header[5];
		body = header[6];

		if (header[0].equals("PUTCHUNK")) { // valid PUTCHUNK message
			chunk = new Chunk(senderId, fileId, chunkNo, body);

			// TODO: Falta colocar aqui o chunkController

			// TODO: Verificar se o diretorio existe
			// Se não existir criar o diretorio que esta
			// especificado no ficheiro Properties

			String filename = chunk.getFileID() + "." + chunk.getChunkNo() + ".chunk";
			File file = new File(filename);

			Path p_file = Paths.get(filename);
			if (chunk.getBodyData().length() > 0) {
				Files.write(p_file, chunk.getBodyData().getBytes());
			}
		}*/
	}

}
