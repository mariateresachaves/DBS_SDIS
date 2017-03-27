package Service.Listeners;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import Utils.Util;

public class DatedMessage {

	private String message;
	private long time;

	public DatedMessage(String message, long time) {
		this.message = message;
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return String.format("%s->%s", message, time + "");
	}
}
