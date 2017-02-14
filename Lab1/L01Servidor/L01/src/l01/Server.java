package l01;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author up201104253
 */
public class Server {

    
    private static HashMap<String,String> database =new HashMap<>();
            
            
    public static void main(String[] args) throws SocketException, IOException {
        String port = "8080";
        if (args.length >= 1) {
            port = args[1];
            System.out.println(port);
        }

        DatagramSocket socket=new DatagramSocket(Integer.parseInt(port));
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        //Test
        System.out.println("Listening");
        socket.receive(packet);
        String response= new String(packet.getData());
        System.out.println(response.trim());
        
        String[] payload=response.split(":");
        if(payload[0].trim().equalsIgnoreCase("REGISTER")){
            database.put(payload[2], payload[1]);
            
            for (Map.Entry<String, String> entrySet : database.entrySet()) {
                String key = entrySet.getKey();
                String value = entrySet.getValue();
                System.out.println(key+"\t"+value);
                
            }
            
        }
        System.out.println(response.length());
    }
}
