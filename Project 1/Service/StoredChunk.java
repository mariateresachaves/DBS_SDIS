package Service;

public class StoredChunk {
	
	private String senderID;
	private String fileID;
	private int chunkNo;
	
	public StoredChunk(String senderID, String fileID, int chunkNo) {
		this.senderID = senderID;
		this.fileID = fileID;
		this.chunkNo = chunkNo;
	}
	
	public String getSenderID() {
		return senderID;
	}
	
	public String getFileID() {
		return fileID;
	}
	
	public int getChunkNo() {
		return chunkNo;
	}
}
