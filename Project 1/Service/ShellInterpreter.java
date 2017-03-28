package Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

import Service.Listeners.DatedMessage;
import Service.Listeners.MCCListener;
import Service.Listeners.PacketCollector;
import Service.Protocols.Backup;
import Service.Protocols.Chunk;
import Service.Protocols.Restore;
import Service.Protocols.Deletion;
import Service.Protocols.Reclaim;
import Service.Protocols.State;
import Utils.Util;
import Service.Protocols.SetDisk;

public class ShellInterpreter {

	private Scanner sc = new Scanner(System.in);

	private static MCCListener mcc_listener;
	private static Thread mcc_thread;

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
				Util.getLogger().log(Level.SEVERE, "Invalid arguments at the BACKUP command");
			}
			// Correct number of arguments
			else {
				System.err.println("[!] Setting BACKUP Protocol");
				protoBackup(args);
			}
			break;
		case "RESTORE":
			// Incorrect number of arguments
			if (args.length != 1) {
				System.out.println("Usage: RESTORE <FilePathName>");
				Util.getLogger().log(Level.SEVERE, "Invalid arguments at the RESTORE command");
			}
			// Correct number of arguments
			else {
				System.err.println("[!] Setting RESTORE Protocol");
				protoRestore(args);
			}
			break;
		case "DELETE":
			// Incorrect number of arguments
			if (args.length != 1) {
				System.out.println("Usage: DELETE <FilePathName>");
				Util.getLogger().log(Level.SEVERE, "Invalid arguments at the DELETE command");
			}
			// Correct number of arguments
			else {
				System.err.println("[!] Setting DELETE Protocol");
				protoDelete(args);
			}
			break;
		case "RECLAIM":
			// Incorrect number of arguments
			if (args.length != 0) {
				System.out.println("Usage: RECLAIM");
				Util.getLogger().log(Level.SEVERE, "Invalid arguments at the RECLAIM command");
			}
			// Correct number of arguments
			else {
				System.err.println("[!] Setting RECLAIM Protocol");
				protoReclaim(args);
			}
			break;
		case "STATE":
			// Incorrect number of arguments
			if (args.length != 0) {
				System.out.println("Usage: STATE");
				Util.getLogger().log(Level.SEVERE, "Invalid arguments at the STATE command");
			}
			// Correct number of arguments
			else {
				System.err.println("[!] Setting STATE Protocol");
				protoState(args);
			}
			break;
		case "SETDISK": // To define the disk space that can be used
			// Incorrect number of arguments
			if (args.length != 1) {
				System.out.println("Usage: SETDISK <MaximumDiskSpace> [KBytes]");
				Util.getLogger().log(Level.SEVERE, "Invalid arguments at the SETDISK command");
			}
			// Correct number of arguments
			else {
				System.err.println("[!] Setting SETDISK Protocol");
				protoSetDisk(args);
			}
			break;
		case "QUIT": // To exit the shell interpreter
			// Incorrect number of arguments
			if (args.length != 0) {
				System.out.println("Usage: QUIT");
				Util.getLogger().log(Level.SEVERE, "Invalid arguments at the QUIT command");
			}
			// Correct number of arguments
			else {
				Util.getLogger().log(Level.WARNING, "Quitting");
				System.exit(0);
			}
			break;
		default:
			Util.getLogger().log(Level.WARNING, "No Protocol Implementation");
			break;
		}

		return 0;
	}

	private void protoSetDisk(String[] args) {
		Util.getLogger().log(Level.INFO, "Running State Protocol");
		SetDisk controller = new SetDisk(args[0]);

		// Get Value from ARGS
		int value;
		try {
			value = Integer.parseInt(args[0].trim());
		} catch (NumberFormatException e) {
			Util.getLogger().log(Level.WARNING, "Incorrect Number Format, aborting");
			return;
		}

		controller.setValue(value);
	}

	private void protoState(String[] args) {
		Util.getLogger().log(Level.INFO, "Running State Protocol");
		State controller = new State();
	}

	private void protoReclaim(String[] args) {
		Util.getLogger().log(Level.INFO, "Running Reclaim Protocol");
		Reclaim controller = new Reclaim();
	}

	private void protoDelete(String[] args) throws IOException {
		Util.getLogger().log(Level.INFO, "Running Delete Protocol");
		Deletion controller = new Deletion(args[0]);

		controller.send_delete();

		// TODO:
		/*
		 * This message does not elicit any response message. An implementation,
		 * may send this message as many times as it is deemed necessary to
		 * ensure that all space used by chunks of the deleted file are deleted
		 * in spite of the loss of some messages.
		 */
	}

	private void protoRestore(String[] args) throws IOException {
		Util.getLogger().log(Level.INFO, "Running Restore Protocol");
		Restore controller = new Restore(args[0]);

		controller.send_getchunk();

		// TODO:
		/*
		 * Upon receiving this message, a peer that has a copy of the specified
		 * chunk shall send it in the body of a CHUNK message via the MDR
		 * channel: CHUNK <Version> <SenderId> <FileId> <ChunkNo>
		 * <CRLF><CRLF><Body>
		 */

		/*
		 * To avoid flooding the host with CHUNK messages, each peer shall wait
		 * for a random time uniformly distributed between 0 and 400 ms, before
		 * sending the CHUNK message. If it receives a CHUNK message before that
		 * time expires, it will not send the CHUNK message.
		 */
	}

	private void protoBackup(String[] args) throws Exception {
		Util.getLogger().log(Level.INFO, "Running Backup Protocol");

		Backup controller = new Backup(args[0], args[1]);
		List<Chunk> chunks = controller.get_chunks();
		ArrayList<DatagramPacket> packets = new ArrayList<DatagramPacket>();
		int num_stores = 0, tries = 0, time = 1000;
		boolean done = false;

		int k = 1;
		for (Chunk chunk : chunks) {
			System.out.println("Chunk -- " + (k++) + " --");
			while (!done && tries != 5) {
				System.out.println("--- Try " + (tries + 1) + " ---");
				int i = chunk.getReplicationDegree();

				while (i > 0) {
					DatagramPacket packet = controller.make_packet(chunk);
					controller.send_putchunk(packet);
					i--;
				}

				// Collects confirmation messages during an interval
				mcc_listener = new MCCListener(time);
				mcc_thread = new Thread(mcc_listener);
				mcc_thread.start();
				//mcc_thread.join();

				//PacketCollector collectedMessages = mcc_listener.getCollectedMessages();
				
				//System.out.println("Mensagens recebidas_ " + collectedMessages.numPutchunks());

				if (num_stores >= chunk.getReplicationDegree())
					done = true;

				// number of confirmation messages received lower than the
				// desired replication degree
				else {
					// doubles the time interval for receiving confirmation
					// messages
					time = time * 2;
					tries++;
				}
			}
		}

		// TODO:
		/*
		 * The initiator-peer collects the confirmation messages during a time
		 * interval of one second. If the number of confirmation messages it
		 * received up to the end of that interval is lower than the desired
		 * replication degree, it retransmits the backup message on the MDB
		 * channel, and doubles the time interval for receiving confirmation
		 * messages. This procedure is repeated up to a maximum number of five
		 * times, i.e. the initiator will send at most 5 PUTCHUNK messages per
		 * chunk.
		 */
	}

}
