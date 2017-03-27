package Service.Listeners;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import Utils.Util;

public class PacketCollector extends ArrayList<DatedMessage> {

	public void deleteOlderThan(long time) {
		long currentTime = System.currentTimeMillis();
		ArrayList<DatedMessage> toBeRemoved = new ArrayList();

		for (DatedMessage x : this) {
			if ((x.getTime() + time) < currentTime) {
				toBeRemoved.add(x);
			}
		}

		this.removeAll(toBeRemoved);
	}

	public ArrayList<String> getLastMessages() {
		ArrayList<String> ret = new ArrayList();

		for (DatedMessage x : this) {

			ret.add(x.getMessage());

		}
		return ret;
	}

}
