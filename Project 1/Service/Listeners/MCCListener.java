package Service.Listeners;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.file.Files;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import Service.Peer;
import Utils.Util;
import Utils.Util.ErrorCode;

public class MCCListener implements Runnable {

	private PacketCollector collectedMessages;
	private String channelIP;
	private String channelport;

	private MulticastSocket mcast_socket;

	private long ttl;
	private long start_time;

	public MCCListener() {
		// Variables assingment
		channelIP = Util.getProperties().getProperty("MC_IP", "224.13.3.1");
		channelport = Util.getProperties().getProperty("MC_PORT", "9176");

		collectedMessages = new PacketCollector();

		start_time = System.currentTimeMillis();
		ttl = Integer.parseInt(Util.getProperties().getProperty("TTL", "1000"));

		/**
		 * Create threads to clear old messages in queuueueueueu
		 */
		Timer t = new Timer();
		TimerTask clearMessagesinQueu = new TimerTask() {
			@Override
			public void run() {
				synchronized (collectedMessages) {
					collectedMessages.deleteOlderThan(ttl);
				}
			}
		};
		t.scheduleAtFixedRate(clearMessagesinQueu, 0, 1000);

	}

	public void run() {
		Util.getLogger().log(Level.INFO, "Starting Multicast Control Channel Listener\n");

		try {
			mcast_socket = new MulticastSocket(Integer.parseInt(this.channelport));
			mcast_socket.joinGroup(InetAddress.getByName(this.channelIP));
		} catch (Exception e) {
			Util.getLogger().log(Level.SEVERE, "Error creating Listener for multicast Restore Channel\n");
			System.exit(ErrorCode.ERR_CREATELISTMCC.ordinal());
		}

		recieveMessage();
	}

	public void recieveMessage() {
		byte[] buf = new byte[4096];
		DatagramPacket packet_received = new DatagramPacket(buf, buf.length);

		// (System.currentTimeMillis() - start_time) -> elapsed time
		while (true) {
			try {
				mcast_socket.receive(packet_received);
			} catch (IOException e) {
				Util.getLogger().log(Level.SEVERE, "Error Recieving packet on control channel\n");
				System.exit(ErrorCode.ERR_MCC_PACKET.ordinal());
			}

			String response = new String(packet_received.getData());
			String protocolMessage = processProtocol(response);

			synchronized (collectedMessages) {
				DatedMessage d_msg = new DatedMessage(response, System.currentTimeMillis());
				collectedMessages.add(d_msg);
			}

			selectProtocol(protocolMessage);
		}

	}

	private void selectProtocol(String protocolMessage) {
		switch (protocolMessage.split(" ")[0]) {

		case "GETCHUNK":
			getChunkProtocol(protocolMessage);
			break;
		case "DELETE":
			deleteProtocol(protocolMessage);
			break;
		case "REMOVED":
			removedProtocol(protocolMessage);
			break;
		case "STORED":
			storedProtocol(protocolMessage);
			break;
		}
	}

	private void storedProtocol(String message) {
		String[] split = message.split(" ");
		String senderId = split[2];
		String fileId = split[3];
		String chunkNo = split[4].trim();

		if (Peer.xmldb.isChunkPresent(senderId, fileId, chunkNo)) {
			Peer.xmldb.addToChunkRD(1, fileId, chunkNo);
		}
	}

	private void removedProtocol(String message) {
		Util.getLogger().log(Level.INFO, "Updating chunk replication degree\n");
		String[] split = message.split(" ");
		String senderId = split[2].trim();
		String fileId = split[3].trim();
		String chunkNo = split[4].trim();
		System.out.println(senderId);
		System.out.println(fileId);
		System.out.println(chunkNo);

		if (Peer.xmldb.isChunkPresent(senderId, fileId, chunkNo)) {
			System.out.println("AQUI");
			Peer.xmldb.addToChunkRD(-1, fileId, chunkNo);
		}
	}

	private void deleteProtocol(String message) {
		String[] split = message.split(" ");
		String fileId = split[3].trim();

		// Check if file is present
		String fileChunksPath = Util.getProperties().getProperty("ChunksLocation", "./chunks_storage");
		String filePath = fileChunksPath + "/" + fileId + "/";

		File f = new File(filePath);
		// Sinceramente nao sei se preciso de esvaziar a pasta primeiro but
		// hey... code...
		if (f.exists() && f.isDirectory()) {
			for (File c : f.listFiles()) {
				c.delete();
			}
			f.delete();

		}

		// Deletes file from db
		Peer.xmldb.deleteChunk(fileId);
		Peer.xmldb.deleteFile(fileId);
	}

	private void getChunkProtocol(String message) {
		String[] split = message.split(" ");
		String version = split[1];
		String senderId = split[2];
		String fileId = split[3];
		String chunkNo = split[4];

		// Check if file is present
		String fileChunksPath = Util.getProperties().getProperty("ChunksLocation", "./chunks_storage");
		String filePath = fileChunksPath + "/" + fileId + "/" + senderId + "-"
				+ String.format("%09d", Integer.parseInt(chunkNo));
		// TESTE
		System.out.println(filePath);

		File f = new File(filePath);
		if (f.exists() && f.canRead()) {
			sendRestorePacket(f, version, fileId, chunkNo);
		}

	}

	private void sendRestorePacket(File f, String version, String FileId, String chunkNo) {
		String hostname;
		int port;
		InetAddress address;

		String tmp_msg = null;
		byte[] msg;

		try {
			DatagramSocket socket = new DatagramSocket();

			// MDB() Channel
			hostname = Util.getProperties().getProperty("MDR_IP");
			port = Integer.parseInt(Util.getProperties().getProperty("MDR_PORT"));
			address = InetAddress.getByName(hostname);
			String senderId = Util.getProperties().getProperty("SenderID");

			// Create message to send
			String body;

			// body = new String(Files.readAllBytes(f.toPath()));
			File ftemp = new File(f.getAbsolutePath());
			Scanner scf = new Scanner(ftemp);

			body = readFileContents(scf);

			tmp_msg = String.format("CHUNK %s %s %s %09d \r\n\r\n %s", version, senderId, FileId,
					Integer.parseInt(chunkNo), body);
			// TESTE

			System.out.println(body);
			// TESTE
			msg = tmp_msg.getBytes();

			DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port);

			// Delay
			// delay random time 0-400ms
			Random r = new Random();
			int delay = Integer.parseInt(Utils.Util.getProperties().getProperty("DELAY", "400"));
			Thread.sleep(r.nextInt(delay));

			socket.send(packet);

			socket.close();
		} catch (IOException | InterruptedException e) {
			Util.getLogger().log(Level.WARNING,
					"Something went wrong sending the recovery packet, printing stack trace");
			e.printStackTrace();
		}

	}

	private String readFileContents(Scanner scf) {
		String file = "";

		while (scf.hasNext()) {
			file += scf.nextLine();
		}

		return file;
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

	public PacketCollector getCollectedMessages() {
		return collectedMessages;
	}

}
