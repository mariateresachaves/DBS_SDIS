package Service.Protocols;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

import Service.Peer;
import Service.StoredChunk;
import Service.Listeners.MCCListener;
import Service.Listeners.MDRListener;
import Service.Listeners.PacketCollector;
import Utils.Util;

public class Restore {

	// private static ArrayList<StoredChunk> chunks_info;
	private int numOfChunks = 0;
	private String fileIDToRestore;
	private String path;
	private String version;

	private static String hostname;
	private static int port;
	private static InetAddress address;

	public Restore(String filePathName) {
		// chunks_info = Peer.getStoredChunk(filePathName);
		numOfChunks = Peer.xmldb.getFilePartSize(filePathName);
		fileIDToRestore = Peer.xmldb.getFileID(filePathName);
		path = filePathName.substring(filePathName.lastIndexOf("/"));
		version = "1.0";
	}

	public void send_getchunk() throws IOException {
		Util.getLogger().log(Level.INFO, "Sending GETCHUNK to MC Channel\n");

		String tmp_msg = null;
		byte[] msg;

		// Socket to send the message
		DatagramSocket socket = new DatagramSocket();

		// MDB(MC?) Channel
		hostname = Util.getProperties().getProperty("MC_IP");
		port = Integer.parseInt(Util.getProperties().getProperty("MC_PORT"));
		address = InetAddress.getByName(hostname);

		// Create message to send
		for (int i = 0; i < numOfChunks; i++) {
			tmp_msg = String.format("GETCHUNK %s %s %s %d \r\n\r\n", version,
					Utils.Util.getProperties().getProperty("SenderID"), fileIDToRestore, i);

			msg = tmp_msg.getBytes();

			DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port);
			socket.send(packet);
		}

		socket.close();
	}

	public boolean assemblyFile() {
		int sleep=Integer.parseInt(Utils.Util.getProperties().getProperty("SleepOnInsuccessRestore", "20000"));
		int retries=Integer.parseInt(Utils.Util.getProperties().getProperty("RetriesOnRestore", "5"));
		for(int i=0;i<retries;i++){
			
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(restoreFile()){
				return true;
			}
		}
		return false;
		

	}

	private boolean restoreFile() {

		ArrayList<String> rest = MDRListener.getRestores(fileIDToRestore);
		String ass = "";

		for (int i = 0; i < numOfChunks; i++) {
			String ch = getChunkWithNo(rest, i);
			if (ch.equalsIgnoreCase("")) {
				//System.out.println("\t\t\tError");
				int sleep=Integer.parseInt(Utils.Util.getProperties().getProperty("SleepOnInsuccessRestore", "20000"));
				System.out.println("Looks Like I am not ready to compile the file Sleeping for "+sleep+" ms");
				return false;
			} else {
				ass +=ch;
			}

		}

		//System.out.println("BUILDING-->"+ass);
		
		FileOutputStream fos;
		try {
			//Verificacao da pasta de recovery
			File fd= new File(Utils.Util.getProperties().getProperty("Recovery"));
			if(!fd.exists()){
				fd.mkdir();
			}
			
			
			fos = new FileOutputStream(Utils.Util.getProperties().getProperty("Recovery") + "/" + path);
			byte[] bfout = stringSplitByPair(ass);

			fos.write(bfout);
			
			fos.flush();
			fos.close();
		} catch (IOException e) {
			Utils.Util.getLogger().log(Level.WARNING, "Error Recovering file");
			e.printStackTrace();
		}

		return true;
		
	}

	private byte[] stringSplitByPair(String ass) {
		String temp = "";
		for (int i = 0; i <= (ass.length() - 2); i = i + 2) {
			temp += (ass.charAt(i)+"") + (ass.charAt(i+1) + " ");
		}

		String[] sp = temp.trim().split(" ");

		// http://stackoverflow.com/questions/18832812/string-of-bytes-to-byte-array

		byte[] bytes = new byte[sp.length];
		for (int i = 0; i < bytes.length; ++i) {
			if(sp[i].equals("")){
				continue;
			}
			bytes[i] = (byte)( Integer.parseInt(sp[i].trim(), 16) );
			
		}
		return bytes;

	}

	public String getChunkWithNo(ArrayList<String> msgs, int i) {
		for (String x : msgs) {
			String[] split = x.split(" ");
			int valorPercorrido = Integer.parseInt(split[4].trim());
			if (valorPercorrido == i) {
				//System.out.println("A RETORNAR!!-> "+split[6].trim());
				return split[6].trim();
			}
		}
		return "";
	}

}
