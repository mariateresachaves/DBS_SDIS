package Service.Protocols;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import Utils.Util;
import Utils.Util.ErrorCode;

public class ChunkController {

	public ChunkController() {
	}

	public List<Chunk> breakIntoChunks(File f, int sizeOfChunk, int replicationDegree)
			throws FileNotFoundException, Exception {
		ArrayList<Chunk> ret = new ArrayList<>();

		if (sizeOfChunk < 0) {
			Util.getLogger().log(Level.SEVERE, "Incorrect size of Chunk\n");
			System.exit(ErrorCode.ERR_SIZECHUNK_CHCONTROLLER.ordinal());
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
					chunkData = new byte[bytes2write];
					System.arraycopy(fileBytes, i, chunkData, 0, bytes2write);
					bytes2write -= bytes2write;
					i += bytes2write;
				}
				ret.add(makeChunk(f, chunkNo, replicationDegree, chunkData));
			}
		} else {
			Util.getLogger().log(Level.SEVERE, "Cannot read file\n");
			System.exit(ErrorCode.ERR_SIZECHUNK_CHCONTROLLER.ordinal());
		}
		return ret;
	}

	private Chunk makeChunk(File f, int chunkNo, int replicationDegree, byte[] data) throws Exception {
		String crypto = Util.getProperties().getProperty("Hash_Crypto");

		Chunk c = new Chunk(getHID(), Utils.Crypto.getFileHash(f.getPath(), crypto), chunkNo, replicationDegree, data);
		// Base64.getEncoder().withoutPadding().encodeToString(data));

		return c;
	}

	private String getHID() throws SocketException {
		String res = Util.getProperties().getProperty("SenderID");
		return res;
	}

}
