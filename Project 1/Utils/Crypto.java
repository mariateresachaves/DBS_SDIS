package Utils;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Crypto {

	public static String getFileHash(File f, String hash)
			throws NoSuchAlgorithmException, NullPointerException, Exception {
		byte[] res;

		if (hash != null) {
			hash = hash.toUpperCase();
		} else {
			throw new Exception("[-] Invalid String passed to getFileHash function\n");
		}

		MessageDigest md = MessageDigest.getInstance(hash);
		byte[] filebytes = Files.readAllBytes(f.toPath());
		md.update(filebytes);
		res = md.digest();

		// http://www.mkyong.com/java/java-sha-hashing-example/
		// convert the byte to hex format method 1
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < res.length; i++) {
			sb.append(Integer.toString((res[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

	public static String getFileHash(String path, String hash) throws NullPointerException, Exception {
		File f = new File(path);
		return Crypto.getFileHash(f, hash);
	}

}
