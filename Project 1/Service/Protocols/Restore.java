package Service.Protocols;

public class Restore {

	// To recover a chunk, the initiator-peer shall send a message with the
	// following format to the MC:
	// GETCHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>

	// Upon receiving this message, a peer that has a copy of the specified
	// chunk shall send it in the body of a CHUNK message via the MDR channel:
	// CHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body>

	public Restore(String filePathName) {
		
		
		// Just a Test
		System.out.println("File path name: " + filePathName);
	}
}
