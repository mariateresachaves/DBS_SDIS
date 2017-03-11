package Service.Protocols;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

public class ChunkController {

    public ChunkController() {

    }

    public List<Chunk> breakIntoChunks(File f, int sizeOfChunk) throws FileNotFoundException {
        ArrayList<Chunk> ret= new ArrayList<>();
        if (sizeOfChunk < 0 || sizeOfChunk > 64 * 1024) {
            Utils.Utils.getLogger().log(Level.SEVERE, "Incorrect size of Chunk");
            System.exit(Utils.Utils.ERR_SIZECHUNK_CHCONTROLLER);
        }

        if (f.isFile() && f.canRead()) {
            Scanner sf = new Scanner(f);
            //for (int i = 0; i < f.; i++) {
                
            //}
            
            //CODE IT
        } else {
            Utils.Utils.getLogger().log(Level.SEVERE, "Incorrect size of Chunk");
            System.exit(Utils.Utils.ERR_SIZECHUNK_CHCONTROLLER);
        }
        return ret;
    }

}
