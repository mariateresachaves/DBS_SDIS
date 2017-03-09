/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author up201104253
 */
public class Crypto {

    public static String getFileHash(File f, String hash) throws NoSuchAlgorithmException, NullPointerException, Exception {
        byte[] res;
        if (hash != null) {
            hash = hash.toUpperCase();
        } else {
            throw new Exception("[-] Invalid String passed to getFileHash function]");
        }

        res = MessageDigest.getInstance(hash).digest(Files.readAllBytes((f.toPath())));
        String ret = new String(res);
        return ret;
    }
    
    public static String getFileHash(String path,String hash) throws NullPointerException, Exception{
        File f= new File(path);
        return Crypto.getFileHash(f, hash);
    }
}
