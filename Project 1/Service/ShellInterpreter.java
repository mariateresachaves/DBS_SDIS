package Service;

import java.util.Scanner;
import java.util.logging.Level;

import Service.Protocols.Backup;
import Service.Protocols.Restore;
import Service.Protocols.Deletion;
import Service.Protocols.Reclaim;
import Service.Protocols.State;
import Utils.Util;
import Service.Protocols.SetDisk;

public class ShellInterpreter {

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

	private void protoDelete(String[] args) {
		Util.getLogger().log(Level.INFO, "Running Delete Protocol");
		Deletion controller = new Deletion(args[0]);
	}

	private void protoRestore(String[] args) {
		Util.getLogger().log(Level.INFO, "Running Restore Protocol");
		Restore controller = new Restore(args[0]);
	}

	private void protoBackup(String[] args) throws Exception {
		Util.getLogger().log(Level.INFO, "Running Backup Protocol");
		Backup controller = new Backup(args[0], args[1]);
	}

}
