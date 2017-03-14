package javarmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {

	public static int ERR_INV_ARGS = 1;
	public static int ERR_LOOKUP = 2;
	public static int ERR_REGISTER = 3;
	
	public static String response;

	private RMIClient() {
	}

	public static void main(String[] args) {
		// Arguments verification
		if (args.length < 4) {
			System.out.println("Usage: java Client <host_name> <remote_object_name> <oper> <opnd> *");
			System.err.println("[-] Wrong number of arguments");
			System.exit(ERR_INV_ARGS);
		}

		String host = args[0].trim();
		String remote_object = args[1].trim();

		String oper = args[2].trim().toUpperCase();
		String[] opnd = new String[args.length - 3];

		System.arraycopy(args, 3, opnd, 0, args.length - 3);

		// Plate format verification
		if (!args[3].matches("[A-Za-z0-9]{2}-[A-Za-z0-9]{2}-[A-Za-z0-9]{2}")) {
			System.err.println("[-] Wrong plate format");
			System.exit(3);
		}

		try {
			Registry registry = LocateRegistry.getRegistry(host);
			Server stub = (Server) registry.lookup(remote_object);

			if (oper.equals("LOOKUP")) {
				response = stub.lookup(opnd[0]);

				if (response.equals("NOT_FOUND")) {
					System.err.println("[-] Plate not registered");
					System.exit(ERR_LOOKUP);
				}
				else
					System.out.println("[+] Plate owner: " + response);
			} else if (oper.equals("REGISTER")) {
				response = stub.register(opnd[0], opnd[1]);

				if (response.equals("-1")) {
					System.err.println("[-] Plate already registered");
					System.exit(ERR_REGISTER);
				}
				else if (response.equals("0"))
					System.out.println("[+] Plate registered successfuly");
					
			}
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}
}