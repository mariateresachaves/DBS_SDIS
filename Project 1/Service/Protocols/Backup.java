package Service.Protocols;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Backup {

	// PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg>
	// <CRLF><CRLF><Body>
	// Chunk(String senderID, String fileID, int chunkNo, String bodyData)

	private static String version;
	private static String senderId;
	private static String fileId;
	private static int chunkNo;
	private static String replicationDeg;
	private static String body;
	private static Chunk chunk;

	public Backup(byte[] message) throws IOException {
		String s_message = new String(message);
		String[] header = s_message.split(" ");
		
		version = header[1];
		senderId = header[2];
		fileId = header[3];
		chunkNo = Integer.parseInt(header[4]);
		replicationDeg = header[5];
		body = header[6];
		
		if(header[0].equals("PUTCHUNK")) { // valid PUTCHUNK message
			chunk = new Chunk(senderId, fileId, chunkNo, body);
			
			// TODO: Falta colocar aqui o chunkController
			
			// TODO: Verificar se o diretorio existe
			//		 Se não existir criar o diretorio que esta
			//		 especificado no ficheiro Properties
			
			String filename = chunk.getFileID() + "." + chunk.getChunkNo() + ".chunk";
			File file = new File(filename);
			
			Path p_file = Paths.get(filename);
			if (chunk.getBodyData().length() > 0) {
				Files.write(p_file, chunk.getBodyData().getBytes());
			}
		}
	}

}
