package Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;

import Service.Listeners.MDBListener;
import Service.Listeners.PacketCollector;
import Service.Protocols.Backup;
import Service.Protocols.Chunk;
import Service.Protocols.Restore;
import Service.Protocols.Deletion;
import Service.Protocols.Reclaim;
import Utils.Util;
import Service.Protocols.SetDisk;

public class ShellInterpreter implements ConnectBackInterface {

	private Scanner sc = new Scanner(System.in);

	public ShellInterpreter() {

	}

	public void getShell() throws Exception {
		while (true) {
			String cmd;
			System.out.print(">");
			cmd = sc.nextLine();
			cmd = cmd.trim();

			String[] tmp = cmd.split(" ", 2);
			String command = tmp[0];
			String[] args = new String[0];

			if (tmp.length != 1)
				args = tmp[1].split(" ");

			inputCommand(command, args);
		}
	}

	private int inputCommand(String cmd, String[] args) throws Exception {
		switch (cmd.toUpperCase()) {
		case "BACKUP":
			// Incorrect number of arguments
			if (args.length != 2) {
				System.out.println("Usage: BACKUP <FilePathName> <ReplicationDegree>");
				Util.getLogger().log(Level.SEVERE, "Invalid arguments at the BACKUP command\n");
			}
			// Correct number of arguments
			else {
				System.err.println("[!] Setting BACKUP Protocol\n");
				protoBackup(args);
			}
			break;
		case "RESTORE":
			// Incorrect number of arguments
			if (args.length != 1) {
				System.out.println("Usage: RESTORE <FilePathName>");
				Util.getLogger().log(Level.SEVERE, "Invalid arguments at the RESTORE command\n");
			}
			// Correct number of arguments
			else {
				System.err.println("[!] Setting RESTORE Protocol\n");
				protoRestore(args);
			}
			break;
		case "DELETE":
			// Incorrect number of arguments
			if (args.length != 1) {
				System.out.println("Usage: DELETE <FilePathName>");
				Util.getLogger().log(Level.SEVERE, "Invalid arguments at the DELETE command\n");
			}
			// Correct number of arguments
			else {
				System.err.println("[!] Setting DELETE Protocol\n");
				protoDelete(args);
			}
			break;
		case "RECLAIM":
			// Incorrect number of arguments
			if (args.length != 0) {
				System.out.println("Usage: RECLAIM");
				Util.getLogger().log(Level.SEVERE, "Invalid arguments at the RECLAIM command\n");
			}
			// Correct number of arguments
			else {
				System.err.println("[!] Setting RECLAIM Protocol\n");
				protoReclaim(args);
			}
			break;
		case "STATE":
			// Incorrect number of arguments
			if (args.length != 0) {
				System.out.println("Usage: STATE");
				Util.getLogger().log(Level.SEVERE, "Invalid arguments at the STATE command\n");
			}
			// Correct number of arguments
			else {
				System.err.println("[!] Setting STATE Protocol\n");
				protoState(args);
			}
			break;
		case "SETDISK": // To define the disk space that can be used
			// Incorrect number of arguments
			if (args.length != 1) {
				System.out.println("Usage: SETDISK <MaximumDiskSpace> [KBytes]");
				Util.getLogger().log(Level.SEVERE, "Invalid arguments at the SETDISK command\n");
			}
			// Correct number of arguments
			else {
				System.err.println("[!] Setting SETDISK Protocol\n");
				protoSetDisk(args);
			}
			break;
		case "QUIT": // To exit the shell interpreter
			// Incorrect number of arguments
			Peer.xmldb.saveDatabase();
			if (args.length != 0) {
				System.out.println("Usage: QUIT");
				Util.getLogger().log(Level.SEVERE, "Invalid arguments at the QUIT command\n");
			}
			// Correct number of arguments
			else {
				Util.getLogger().log(Level.WARNING, "Quitting\n");
				System.exit(0);
			}
			break;
		default:
			Util.getLogger().log(Level.WARNING, "No Protocol Implementation\n");
			break;
		}

		return 0;
	}

	private void protoSetDisk(String[] args) {
		Util.getLogger().log(Level.INFO, "Running State Protocol\n");
		SetDisk controller = new SetDisk(args[0]);

		// Get Value from ARGS
		int value;
		try {
			value = Integer.parseInt(args[0].trim());
		} catch (NumberFormatException e) {
			Util.getLogger().log(Level.WARNING, "Incorrect Number Format, aborting\n");
			return;
		}

		controller.setValue(value);
	}

	private void protoState(String[] args) {
		Util.getLogger().log(Level.INFO, "Running State Protocol\n");
		ArrayList<String> files = Peer.xmldb.getFiles();
		ArrayList<String> chunks = Peer.xmldb.getChunks();

		System.out.println("Files initiated from this endpoint\n");

		for (String x : files) {
			System.out.println(x);
		}

		System.out.println("Chunks stored in this endpoint\n");
		for (String x : chunks) {
			System.out.println(x);
		}
	}

	private void protoReclaim(String[] args) throws IOException {
		Util.getLogger().log(Level.INFO, "Running Reclaim Protocol\n");

		// Chunk to be deleted
		ArrayList<String> filesInfo = Peer.xmldb.getChunksInfo();

		for (String fileInfo : filesInfo) {
			String[] split = fileInfo.split(" ");
			String fileId = split[0];
			String desiredRD = split[1];
			String RD = split[2];
			String chunkNo = split[3];
			String senderId = split[4].trim();

			if (Integer.parseInt(desiredRD) < Integer.parseInt(RD)) {
				Util.getLogger().log(Level.INFO, "Deleting chunk No " + chunkNo + "\n");
				MDBListener.deleteChunk(fileId, senderId, chunkNo);

				Reclaim controller = new Reclaim(senderId, fileId, Integer.parseInt(chunkNo));
				controller.send_removed();
				break;
			}
		}
	}

	private void protoDelete(String[] args) throws IOException {
		Util.getLogger().log(Level.INFO, "Running Delete Protocol\n");
		Deletion controller = new Deletion(args[0]);

		controller.send_delete();
	}

	private void protoRestore(String[] args) throws IOException {
		Util.getLogger().log(Level.INFO, "Running Restore Protocol\n");
		Restore controller = new Restore(args[0]);

		controller.send_getchunk();

		System.out.println("Request Packets Sent, Waiting for replies, this could take a while, go grab a coffe");
		controller.assemblyFile();
	}

	private void protoBackup(String[] args) throws Exception {
		Util.getLogger().log(Level.INFO, "Running Backup Protocol\n");

		Backup controller = new Backup(args[0], args[1]);
		List<Chunk> chunks = controller.get_chunks();
		int num_stores = 0, tries = 0;
		long time;
		boolean done = false;

		// Save on the database
		if (!Peer.xmldb.isFilePresent(args[0], chunks.get(0).getFileID()))
			Peer.xmldb.addFile(args[0], chunks.get(0).getFileID(), chunks.get(0).getReplicationDegree() + "", "0");

		int k = 1;
		for (Chunk chunk : chunks) {
			System.out.println("[ ~~~~~~ Chunk " + (k++) + " ~~~~~~ ]\n");

			time = 1000;

			while (!done && tries != 5) {
				System.out.println("[ ~~~~~~ Try " + (tries + 1) + " ~~~~~~ ]\n");
				int i = chunk.getReplicationDegree();

				while (i > 0) {
					DatagramPacket packet = controller.make_packet(chunk);

					// Adicionar part à base de dados
					if (!Peer.xmldb.isPartPresent(args[0], chunks.get(0).getFileID(), chunk.getChunkNo()))
						Peer.xmldb.addFilePart(args[0], chunks.get(0).getFileID(), chunk.getChunkNo(), 0);

					controller.send_putchunk(packet);

					// delay random time 0-400ms
					Random r = new Random();
					Thread.sleep(r.nextInt(400));

					i--;
				}

				// Collects confirmation messages during an interval
				PacketCollector msgs = Peer.mccl.getCollectedMessages();

				// Counts number of STOREs received
				num_stores = msgs.numStores(chunk.getFileID(), chunk.getChunkNo() + "", time);

				if (Peer.xmldb.isPartPresent(args[0], chunks.get(0).getFileID(), chunk.getChunkNo()))
					Peer.xmldb.updateFilePart(args[0], chunks.get(0).getFileID(), chunk.getChunkNo(), num_stores);

				if (num_stores >= chunk.getReplicationDegree()) {
					done = true;
					Util.getLogger().log(Level.INFO, "Chunk No " + (chunk.getChunkNo() + 1) + " Stored Correctly\n");
				}

				// number of confirmation messages received lower than the
				// desired replication degree
				else {
					// doubles the time interval for receiving confirmation
					// messages
					Util.getLogger().log(Level.INFO, "Going to try again with a period of " + time + "ms");
					time = time * 2;
					tries++;
				}
			}

			tries = 0;
			done = false;
		}
	}

	@Override
	public String sendCommand(String proto, String[] args) throws RemoteException {
		try {
			int value = this.inputCommand(proto, args);
			return "Command Sent Successfully" + value;
		} catch (Exception e) {
			return "Something went wrong processing the request";
		}
	}

	public void startRMI() {
		try {

			ConnectBackInterface stub = (ConnectBackInterface) UnicastRemoteObject.exportObject(this, 0);
			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();

			registry.rebind(Utils.Util.getProperties().getProperty("SenderID", "Agent999"), stub);

			Utils.Util.getLogger().log(Level.INFO, "RMI Server Started");
		} catch (Exception e) {
			Utils.Util.getLogger().log(Level.WARNING, "RMI Server Failed to Start Started");
			e.printStackTrace();
		}
	}
}
