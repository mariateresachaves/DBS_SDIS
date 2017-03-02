package testingapp;

/**
 *
 * @author up201104253
 */
public class TestingApp {

	/**
	 * Error codes definition
	 */
	private static int ERR_ARG = 1;
	private static int ERR_CONV_PORT = 2;
	private static int ERR_NO_PROTO = 3;
	private static int ERR_NO_DEG_REP = 3;

	/**
	 * Application Variables
	 *
	 */
	private static String targetIP = "";
	private static int targetPort;

	public static void main(String[] args) {

		// The last opnd only applies to certain protocols
		if (args.length < 3) {
			throwErrorandExit("[-] Wrong Number of arguments, int error", ERR_ARG);
		}

		setDestinations(args[1]);

		// Set Protocols
		String[] newArgs = new String[args.length - 3];
		System.arraycopy(args, 3, newArgs, 0, args.length - 3);
		setProtocol(args[2], newArgs);

	}

	private static void throwErrorandExit(String msg, int error) {
		System.err.println(msg);
		System.exit(error);
	}

	private static void setDestinations(String arg) {
		// Check the target
		// Check if an IP/Port is specified
		if (arg.contains(":")) {
			String[] dst = arg.split(":");
			targetIP = dst[0];
			try {
				targetPort = Integer.parseInt(dst[1]);
			} catch (Exception e) {
				throwErrorandExit("[-] Error converting to int from string @ port", ERR_CONV_PORT);
			}
		} else {
			targetIP = "127.0.0.1";
			try {
				targetPort = Integer.parseInt(arg);
			} catch (Exception e) {
				throwErrorandExit("[-] Error converting to int from string @ port", ERR_CONV_PORT);
			}
		}
	}

	private static void setProtocol(String arg, String[] opnd) {
		arg = arg.toUpperCase();
		switch (arg) {
		case "BACKUP":
			System.err.println("[!] Setting BACKUP Protocol");
			protoBackup(opnd);
			break;
		case "RESTORE":
			System.err.println("[!] Setting RESTORE Protocol");
			protoRestore(opnd);
			break;
		case "DELETE":
			System.err.println("[!] Setting DELETE Protocol");
			protoDelete(opnd);
			break;
		case "RECLAIM":
			System.err.println("[!] Setting RECLAIM Protocol");
			protoReclaim(opnd);
			break;
		default:
			throwErrorandExit("[-] No protocol implemented", ERR_NO_PROTO);
			break;
		}
	}

	private static void protoBackup(String[] opnd) {
		if (opnd.length < 2) {
			throwErrorandExit("[-] No replication degree specified", ERR_NO_DEG_REP);
		}
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	private static void protoRestore(String[] opnd) {
		String opt = opnd[0];
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	private static void protoDelete(String[] opnd) {
		String opt = opnd[0];
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	private static void protoReclaim(String[] opnd) {
		String opt = opnd[0];
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}
}
