import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import Service.ConnectBackInterface;


public class TestingApp {

	public static int ERR_INV_ARGS = 1;
	public static int ERR_LOOKUP = 2;
	public static int ERR_REGISTER = 3;

	public static String response;

	public static void main(String[] args) {
		// Arguments verification
		if (args.length < 3) {
			System.out
					.println("Usage: java TestingApp <host_name> <PROTO> <oper> <opnd>");
			System.err.println("[-] Wrong number of arguments");
			System.exit(ERR_INV_ARGS);
		}

		String host = "localhost";
		String remote_object = args[0].trim();

		String oper = args[2].trim().toUpperCase();
		String[] opnd = new String[args.length - 3];


		String[] remoteArgs=new String[args.length-2];
		System.arraycopy(args, 2, remoteArgs, 0, remoteArgs.length);

		System.out.println("Going to send the following command->" + args[0]);
		try {
			Registry registry = LocateRegistry.getRegistry(host);

			for(String x: registry.list()){
				System.out.println(x);
			}

			ConnectBackInterface stub = (ConnectBackInterface) registry
					.lookup(remote_object);

			response = stub.sendCommand(args[1],remoteArgs );
			System.out.println(response);
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}
}
