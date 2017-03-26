package Service.Protocols;

import java.io.File;
import java.util.Formatter;
import java.util.logging.Level;

public class Chunk {

	private String senderID; // Peer identifier that has sent the chunk
	private String fileID; // This is where the hash of the file if going to be
	private int chunkNo;
	private int replicationDegree;
	private String bodyData;

	public Chunk() {
	}

	public Chunk(String senderID, String fileID, int chunkNo, int replicationDegree, String bodyData) {
		setSenderID(senderID);
		setFileID(fileID);
		setChunkNo(chunkNo);
		setReplicationDegree(replicationDegree);
		setBodyData(bodyData);
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

	public int getReplicationDegree() {
		return replicationDegree;
	}

	public String getBodyData() {
		return bodyData;
	}

	public void setSenderID(String senderID) {
		if (senderID != null && !senderID.equalsIgnoreCase("")) {
			this.senderID = senderID;
		} else {
			Utils.Util.getLogger().log(Level.SEVERE, "Creating a Chunk with NULL Sender ID");
			System.exit(Utils.Util.ERR_NULLSENDERID_CHUCK);
		}
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
		if (fileID != null && !fileID.equalsIgnoreCase("")) {
			this.fileID = fileID;
		} else {
			Utils.Util.getLogger().log(Level.SEVERE, "Creating a Chunk with NULL File ID");
			System.exit(Utils.Util.ERR_NULLFILEID_CHUCK);
		}
	}

	public void setChunkNo(int chunkNo) {
		if (chunkNo >= 0 && chunkNo <= 999999) {
			this.chunkNo = chunkNo;
		} else {
			Utils.Util.getLogger().log(Level.SEVERE, "Creating a Chunk with overflowed or underflowed values");
			System.exit(Utils.Util.ERR_OVER_CHUNK);
		}
	}

	public void setReplicationDegree(int replicationDegree) {
		if (replicationDegree > 0) {
			this.replicationDegree = replicationDegree;
		} else {
			Utils.Util.getLogger().log(Level.SEVERE, "Invalid replication degree");
			System.exit(Utils.Util.ERR_REP_DEGREE);
		}
	}

	public void setBodyData(String bodyData) {
		if (bodyData != null && !bodyData.equalsIgnoreCase("")) {
			this.bodyData = bodyData;
		} else {
			Utils.Util.getLogger().log(Level.SEVERE, "Creating a Chunk with null Value");
			System.exit(Utils.Util.ERR_NULLBODY_CHUCK);
		}
	}

	@Override
	public String toString() {
		return String.format("%s:%s:%s:%s", this.senderID, this.fileID, this.chunkNo, this.bodyData);
	}

	public boolean saveToDisk(String path) {
		File f = new File(path + "/" + this.senderID + "-" + this.fileID + "-" + this.chunkNo);

		try {
			Formatter ft = new Formatter(f);
			ft.format("%s %s %s\n%s", this.senderID, this.fileID, this.chunkNo, this.bodyData);
			ft.flush();
			ft.close();
			return true;
		} catch (Exception e) {
			Utils.Util.getLogger().log(Level.WARNING, "Couln't save chunk to folder");
			return false;
		}
	}

}
