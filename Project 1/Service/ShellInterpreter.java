package Service;

import java.util.Scanner;
import java.util.logging.Level;

import Service.Protocols.Backup;
import Service.Protocols.Restore;
import Service.Protocols.Deletion;
import Service.Protocols.Reclaim;
import Service.Protocols.State;
import Service.Protocols.SetDisk;

public class ShellInterpreter {

	private Scanner sc = new Scanner(System.in);

	public ShellInterpreter() {

	}

	public void getShell() {
		
		while (true) {
			String cmd;
			System.out.print(">");
			cmd=sc.nextLine();
			cmd=cmd.trim();
			
			String command=cmd.split(" ")[0];
			String[] args=new String[command.length()-1];
			inputCommand(command,args);
		}
	}

	private int inputCommand(String cmd,String[] args) {
		switch (cmd) {
		case "BACKUP":
			System.err.println("[!] Setting BACKUP Protocol");
			protoBackup(args);
			break;
		case "RESTORE":
			System.err.println("[!] Setting RESTORE Protocol");
			protoRestore(args);
			break;
		case "DELETE":
			System.err.println("[!] Setting DELETE Protocol");
			protoDelete(args);
			break;
		case "RECLAIM":
			System.err.println("[!] Setting RECLAIM Protocol");
			protoReclaim(args);
			break;
		case "STATE":
			System.err.println("[!] Setting STATE Protocol");
			protoState(args);
			break;
		case "SETDISK": //Para definir o espaço em disco que pode ser utilizado
			System.err.println("[!] Setting SETDISK Protocol");
			protoSetDisk(args);
			break;
		case "QUIT": //Para definir o espaço em disco que pode ser utilizado
			Utils.Utils.getLogger().log(Level.WARNING, "Quitting");
			System.exit(0);
			break;
		default:
			Utils.Utils.getLogger().log(Level.WARNING, "No Protocol Implementation");
			break;
		}

		return 0;
	}

	private void protoSetDisk(String[] args) {
		Utils.Utils.getLogger().log(Level.INFO, "Running State Protocol");
		SetDisk controller = new SetDisk();	
		
	}

	private void protoState(String[] args) {
		Utils.Utils.getLogger().log(Level.INFO, "Running State Protocol");
		State controller = new State();		
	}

	private void protoReclaim(String[] args) {
		Utils.Utils.getLogger().log(Level.INFO, "Running Reclaim Protocol");
		Reclaim controller = new Reclaim();
	}

	private void protoDelete(String[] args) {
		Utils.Utils.getLogger().log(Level.INFO, "Running Delete Protocol");
		Deletion controller = new Deletion();
	}

	private void protoRestore(String[] args) {
		Utils.Utils.getLogger().log(Level.INFO, "Running Restore Protocol");
		Restore controller= new Restore(); 
	}

	private void protoBackup(String[] args) {
		Utils.Utils.getLogger().log(Level.INFO, "Running Backup Protocol");
		//Backup controller= new Backup(message)
		//Um bocado confuso com a implementacao
	}

}
