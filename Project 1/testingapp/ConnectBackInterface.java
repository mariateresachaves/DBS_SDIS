import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ConnectBackInterface extends Remote {
	String sendCommand(String b) throws RemoteException;
}
