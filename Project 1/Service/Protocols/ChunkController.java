package Service.Protocols;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;

import Utils.Util;

public class ChunkController {

	public ChunkController() {

	}

	public List<Chunk> breakIntoChunks(File f, int sizeOfChunk, int replicationDegree)
			throws FileNotFoundException, Exception {
		ArrayList<Chunk> ret = new ArrayList<>();

		if (sizeOfChunk < 0) {
			Util.getLogger().log(Level.SEVERE, "Incorrect size of Chunk");
			System.exit(Utils.Util.ERR_SIZECHUNK_CHCONTROLLER);
		}

		if (f.isFile()) {
			int bytes2write = 0, chunkNo = 0;
			byte[] fileBytes = Files.readAllBytes(f.toPath());
			int fileSize = (int) f.length();

			bytes2write = fileSize;
			for (int i = 0; bytes2write > 0; chunkNo++) {
				byte[] chunkData = new byte[sizeOfChunk];

				// Check if can create a chunk with remaining bytes2write
				if (bytes2write / sizeOfChunk > 0) {
					System.arraycopy(fileBytes, i, chunkData, 0, sizeOfChunk);
					bytes2write -= sizeOfChunk;
					i += sizeOfChunk;
				}
				// File size less than chunk size or file size not pair with
				// chunk size
				else {
					System.arraycopy(fileBytes, i, chunkData, 0, bytes2write);
					bytes2write -= bytes2write;
					i += bytes2write;
				}
				ret.add(makeChunk(f, chunkNo, replicationDegree, chunkData));
			}

		} else {
			Util.getLogger().log(Level.SEVERE, "Cannot read file.");
			System.exit(Utils.Util.ERR_SIZECHUNK_CHCONTROLLER);
		}
		return ret;
	}

	private Chunk makeChunk(File f, int chunkNo, int replicationDegree, byte[] data) throws Exception {

		String crypto = Util.getProperties().getProperty("Hash_Crypto");

		Chunk c = new Chunk(getHID(), Utils.Crypto.getFileHash(f.getPath(), crypto), chunkNo, replicationDegree,
				Base64.getEncoder().encodeToString(data));

		return c;
	}

	private String getHID() throws SocketException {
		/*
		 * Not working
		 * 
		 * String res = "";
		 * System.out.println(NetworkInterface.getNetworkInterfaces().toString()
		 * ); byte[] mac = NetworkInterface.getByIndex(0).getHardwareAddress();
		 * for (int k = 0; k < mac.length; k++) { res += String.format("%02X%s",
		 * mac[k], (k < mac.length - 1) ? "-" : ""); }
		 */
		String res = "011";
		return res;
	}

}
