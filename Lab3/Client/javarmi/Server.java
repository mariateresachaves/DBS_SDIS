package javarmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
	String register(String a, String b) throws RemoteException;
	String lookup(String b) throws RemoteException;
}