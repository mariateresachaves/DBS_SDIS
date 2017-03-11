/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service.Protocols;

import java.util.logging.Level;

/**
 *
 * @author up201104253
 */
public class Chunk {

    private String senderID;
    private String fileID; //This is where the hash of the file if going to be
    private int chunkNo;
    private String bodyData;

    public Chunk() {
    }

    public Chunk(String senderID, String fileID, int chunkNo, String bodyData) {
        setSenderID(senderID);
        setFileID(fileID);
        setChunkNo(chunkNo);
        setBodyData(bodyData);
    }

    public String getBodyData() {
        return bodyData;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public String getFileID() {
        return fileID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setBodyData(String bodyData) {
        if (bodyData!=null && !bodyData.equalsIgnoreCase("")) {
                this.bodyData=bodyData;
        }
        else{
            Utils.Utils.getLogger().log(Level.SEVERE, "Creating a Chunk with null Value");
            System.exit(Utils.Utils.ERR_NULLBODY_CHUCK);
        }
    }

    public void setChunkNo(int chunkNo) {
        if (chunkNo>=0 && chunkNo<=999999) {
                this.chunkNo=chunkNo;
        }
        else{
            Utils.Utils.getLogger().log(Level.SEVERE, "Creating a Chunk with overflowd or underflowed values");
            System.exit(Utils.Utils.ERR_OVER_CHUNK);
        }
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
         if (fileID!=null && !fileID.equalsIgnoreCase("")) {
                this.fileID=fileID;
        }
        else{
            Utils.Utils.getLogger().log(Level.SEVERE, "Creating a Chunk with NULL File ID");
            System.exit(Utils.Utils.ERR_NULLFILEID_CHUCK);
        }
    }

    public void setSenderID(String senderID) {
        if (senderID!=null && !senderID.equalsIgnoreCase("")) {
                this.senderID=senderID;
        }
        else{
            Utils.Utils.getLogger().log(Level.SEVERE, "Creating a Chunk with NULL Sender ID");
            System.exit(Utils.Utils.ERR_NULLSENDERID_CHUCK);
        }
    }

    @Override
    public String toString() {
        return String.format("%s:%s:%s:%s", this.senderID,this.fileID,this.chunkNo,this.bodyData);
    }
    
    

}
