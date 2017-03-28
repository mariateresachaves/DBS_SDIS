package Service.Protocols;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Formatter;
import java.util.logging.Level;

import Utils.Util.ErrorCode;

public class Chunk {

	private String senderID; // Peer identifier that has sent the chunk
	private String fileID; // This is where the hash of the file if going to be
	private int chunkNo;
	private int replicationDegree;
	private byte[] bodyData;

	public Chunk() {
	}

	public Chunk(String senderID, String fileID, int chunkNo, int replicationDegree, byte[] bodyData) {
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

	public byte[] getBodyData() {
		return bodyData;
	}

	public void setSenderID(String senderID) {
		if (senderID != null && !senderID.equalsIgnoreCase("")) {
			this.senderID = senderID;
		} else {
			Utils.Util.getLogger().log(Level.SEVERE, "Creating a Chunk with NULL Sender ID");
			System.exit(ErrorCode.ERR_NULLSENDERID_CHUCK.ordinal());
		}
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
		if (fileID != null && !fileID.equalsIgnoreCase("")) {
			this.fileID = fileID;
		} else {
			Utils.Util.getLogger().log(Level.SEVERE, "Creating a Chunk with NULL File ID");
			System.exit(ErrorCode.ERR_NULLFILEID_CHUCK.ordinal());
		}
	}

	public void setChunkNo(int chunkNo) {
		if (chunkNo >= 0 && chunkNo <= 999999) {
			this.chunkNo = chunkNo;
		} else {
			Utils.Util.getLogger().log(Level.SEVERE, "Creating a Chunk with overflowed or underflowed values");
			System.exit(ErrorCode.ERR_OVER_CHUNK.ordinal());
		}
	}

	public void setReplicationDegree(int replicationDegree) {
		if (replicationDegree > 0) {
			this.replicationDegree = replicationDegree;
		} else {
			Utils.Util.getLogger().log(Level.SEVERE, "Invalid replication degree");
			System.exit(ErrorCode.ERR_REP_DEGREE.ordinal());
		}
	}

	public void setBodyData(byte[] bodyData) {
		if (bodyData != null ) {
			this.bodyData = bodyData;
		} else {
			Utils.Util.getLogger().log(Level.SEVERE, "Creating a Chunk with null Value");
			System.exit(ErrorCode.ERR_NULLBODY_CHUCK.ordinal());
		}
	}

	@Override
	public String toString() {
		return String.format("%s:%s:%s:%s", this.senderID, this.fileID, this.chunkNo, this.bodyData);
	}

	public boolean saveToDisk(String path) {

		File directory = new File(path+"/"+this.fileID);

		if (!directory.exists()) {
			directory.mkdir();
		}

		File f = new File(path+"/"+this.fileID+"/" + this.senderID + "-" + String.format("%09d",this.chunkNo));

		try {
			/*Formatter ft = new Formatter(f);
			ft.format("%s", this.bodyData);
			ft.flush();
			ft.close();
			*/
			FileOutputStream fos = new FileOutputStream(f.getPath());
			fos.write(this.bodyData);
			fos.close();
			
			return true;
		} catch (Exception e) {
			Utils.Util.getLogger().log(Level.WARNING, "Couln't save chunk to folder");
			return false;
		}
	}

}
