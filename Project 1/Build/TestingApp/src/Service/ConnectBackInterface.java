package Service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ConnectBackInterface extends Remote {
	String sendCommand(String proto,String[] args) throws RemoteException;
}
