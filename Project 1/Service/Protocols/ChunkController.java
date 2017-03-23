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

	public List<Chunk> breakIntoChunks(File f, int sizeOfChunk) throws FileNotFoundException, Exception {
		ArrayList<Chunk> ret = new ArrayList<>();
		if (sizeOfChunk < 0 || sizeOfChunk > 64 * 1024) {
			Util.getLogger().log(Level.SEVERE, "Incorrect size of Chunk");
			System.exit(Utils.Util.ERR_SIZECHUNK_CHCONTROLLER);
		}

		if (f.isFile() && f.canRead()) {
			int contador = 0, seek = 0;
			byte[] tempValues = new byte[sizeOfChunk];
			byte temp;
			FileInputStream fip = new FileInputStream(f);
			byte[] fileBytes = Files.readAllBytes(f.toPath());
			// aqui, vou ler e dividir o array, já estou farto

			for (int i = 0; i < fileBytes.length; i++) {

				if (contador >= sizeOfChunk) {
					// check for end of stream

					ret.add(makeChunk(f, tempValues));

					// Reset aos valores
					contador = 0;
					tempValues = new byte[sizeOfChunk];
					tempValues[contador++] = fileBytes[i];
				} else {
					tempValues[contador++] = fileBytes[i];
				}

			}
			if (contador >= 1) {
				// Flush data
				byte[] flushedData = new byte[contador];
				System.arraycopy(tempValues, 0, flushedData, 0, contador);
				ret.add(makeChunk(f, flushedData));
			}

		} else {
			Util.getLogger().log(Level.SEVERE, "Incorrect size of Chunk");
			System.exit(Utils.Util.ERR_SIZECHUNK_CHCONTROLLER);
		}
		return ret;
	}

	private Chunk makeChunk(File f, byte[] data) throws Exception {

		String crypto = Util.getProperties().getProperty("Hash_Crypto");

		Chunk c = new Chunk(getHID(), Utils.Crypto.getFileHash(f.getPath(), crypto), 0,
				Base64.getEncoder().encodeToString(data));
		// Chunk c = new Chunk(getHID(), Utils.Crypto.getFileHash(f.getPath(),
		// "SHA-256"), 0, new String(data));
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
