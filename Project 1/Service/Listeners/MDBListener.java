package Service.Listeners;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;

import Service.Protocols.Chunk;
import Utils.Util;

public class MDBListener implements Runnable {

	private String storage;
	private String channelIP;
	private String channelport;

	public MDBListener() {
		// Variables assingment
		storage = Util.getProperties().getProperty("ChunksLocation", "./chunks");
		channelIP = Util.getProperties().getProperty("MDB_IP", "224.13.3.2");
		channelport = Util.getProperties().getProperty("MDB_PORT", "9177");

		// Check preconditions
		checkStorage(storage);
		// Run Thread
		// this.run();
	}

	public void run() {
		// Set of variables
		MulticastSocket mcast_socket = null;

		try {
			mcast_socket = new MulticastSocket(Integer.parseInt(this.channelport));
			mcast_socket.joinGroup(InetAddress.getByName(this.channelIP));
		} catch (Exception e) {
			Util.getLogger().log(Level.SEVERE, "Error creating Listener for multicast Backup Channel");
			System.exit(Util.ERR_CREATELISTMDB);
		}

		recieveMessage(mcast_socket);
	}

	private void recieveMessage(MulticastSocket sck) {		
		byte[] buf = new byte[4096];
		DatagramPacket packet_received = new DatagramPacket(buf, buf.length);

		try {
			while (true) {

				sck.receive(packet_received);

				String response = new String(packet_received.getData());
				String protocolMessage = processProtocol(response);

				selectProtocol(protocolMessage);

			}
		} catch (Exception e) {
			Util.getLogger().log(Level.WARNING, "Error Recieving packet, Error Message: ");
			e.printStackTrace();
		}

	}

	private void selectProtocol(String protocolMessage) {
		String[] split = protocolMessage.split(" ");

		if (!split[2].trim().equals(Util.getProperties().getProperty("SenderID"))) {
			switch (protocolMessage.split(" ")[0]) {
	
			case "PUTCHUNK":
				Util.getLogger().log(Level.INFO, "Received PUTCHUNK on MDB Channel");
				saveChunk(protocolMessage);
				break;
	
			}
		}
	}

	private void saveChunk(String protocolMessage) {
		String[] split = protocolMessage.split(" ");
		
		if (!split[2].trim().equals(Util.getProperties().getProperty("SenderID"))) {
			Chunk c = new Chunk(split[2], split[3], Integer.parseInt(split[4]), Integer.parseInt(split[5].trim()),
					split[6]);
			
			System.out.println("[+] Saving Chunk No " + split[4]);

			if (c.saveToDisk(this.storage)) {
				anounceStorageonMCC(c);
			}
		}

	}

	private void anounceStorageonMCC(Chunk c) {
		/*Util.getLogger().log(Level.INFO, "Sending STORED to MC Channel");
		
		// STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
		String msg = String.format("STORED %s %s %s %d \r\n", "1.0", c.getSenderID(), c.getFileID(), c.getChunkNo());

		// Data For MCC Channel
		try {
			String mcadd = Util.getProperties().getProperty("MC_IP", "224.13.3.1");
			String mcport = Util.getProperties().getProperty("MC_PORT", "9176");
			DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
					InetAddress.getByName(mcadd), Integer.parseInt(mcport));
			DatagramSocket serverSocket = new DatagramSocket();
			serverSocket.send(msgPacket);
			serverSocket.close();
		} catch (Exception e) {
			Util.getLogger().log(Level.SEVERE, "Error Creating Socket to Send STORED. Error Message " + e.getMessage());
		}*/

	}

	private String processProtocol(String response) {

		String old = response;

		String processed;
		processed = old.replaceAll("  ", " ");
		if (old.equalsIgnoreCase(processed)) {
			return processed;
		} else {
			return processProtocol(processed);
		}

	}

	private void checkStorage(String loc) {
		File f = new File(loc);
		if (f.isDirectory() && f.canRead() && f.canWrite()) {
			Util.getLogger().log(Level.INFO, "Chunk Directory " + loc + " is created/readable/writable");
		} else {
			try {
				f.mkdir();
			} catch (Exception e) {
				Util.getLogger().log(Level.SEVERE, "Error Creating chunk store");
				System.exit(Util.ERR_CHUNKSTORAGE);
			}
		}

	}

}
