ipackage l01;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author up201104253
 */
public class Server {

    private static final HashMap<String, String> database = new HashMap<>();

    public static void main(String[] args) throws SocketException, IOException {
        String port = "8080";
        if (args.length >= 2) {
            port = args[1];
            System.out.println(port);
        }
        System.out.println("Listening");

        DatagramSocket socket = new DatagramSocket(Integer.parseInt(port));
        while (true) {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            //Test
            socket.receive(packet);
            String response = new String(packet.getData());
            System.out.println(response.trim());

            String[] payload = response.split(":");
            if (payload[0].trim().equalsIgnoreCase("REGISTER")) {
                payload[1]=payload[1].trim().toUpperCase();
                if(database.get(payload[1])==null ){
                    database.put(payload[1], payload[2]);
                    byte[] ResponseBuf;
                    ResponseBuf="0".getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(ResponseBuf, ResponseBuf.length,packet.getAddress(),packet.getPort());
                    
                    System.out.println( );
                    socket.send(responsePacket);
                }
                else{
                    byte[] ResponseBuf;
                    ResponseBuf="-1".getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(ResponseBuf, ResponseBuf.length,packet.getAddress(),packet.getPort());
                    System.out.println(ResponseBuf);
                    socket.send(responsePacket);
                }

                for (Map.Entry<String, String> entrySet : database.entrySet()) {
                    String key = entrySet.getKey();
                    String value = entrySet.getValue();
                    System.out.println(key + "\t\t" + value);

                }

            }
            if (payload[0].trim().equalsIgnoreCase("LOOKUP")) {
                payload[1]=payload[1].trim();
                if(database.get(payload[1])==null){
                    byte[] ResponseBuf = new byte[256];
                    ResponseBuf="NOT_FOUND".getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(ResponseBuf, ResponseBuf.length,packet.getAddress(),packet.getPort());
                    socket.send(responsePacket);
                }
                else{
                    byte[] ResponseBuf = new byte[256];
                    ResponseBuf=database.get(payload[1]).getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(ResponseBuf, ResponseBuf.length,packet.getAddress(),packet.getPort());
                    socket.send(responsePacket);
                }
                

            }
            System.out.println(response.length());
        }
    }
}

