/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javarmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class RMIServer implements Server {

    private static int ERR_INV_ARGS=1;
    
    private static final HashMap<String, String> db = new HashMap<>();

    public RMIServer() {
    }

    @Override
    public String register(String a, String b) {
        if (db.get(a.toUpperCase().trim()) == null) {
            db.put(a.toUpperCase(), b);
            return "0";
        } else {
            return "-1";
        }

    }

    @Override
    public String lookup(String a) {
        String ret;
        ret = db.getOrDefault(a.toUpperCase(), "NOT_FOUND");
        return ret;
    }

    public static void main(String args[]) {

        if (args.length < 1) {
            System.out.println("java Server <remote_object_name>");
            System.exit(ERR_INV_ARGS);
        }
        try {
            RMIServer obj = new RMIServer();
            Server stub = (Server) UnicastRemoteObject.exportObject(obj, 1098);
            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();

            registry.rebind(args[0], stub);
//boolean p=abc.equals("1")?true:false;
            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
