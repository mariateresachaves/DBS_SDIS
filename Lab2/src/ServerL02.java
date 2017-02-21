package l02;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class ServerL02 {

    /**
     * Error codes
     */
    public static int ERR_WRONG_ARGS = 1;
    public static int ERR_SENDING_ADV = 2;

    /**
     * Socket-related variables
     */
    private static DatagramSocket serverSocket;
    private static InetAddress addr;
    private static int srvc_port;
    private static String mcast_addr;
    private static int mcast_port;

    public static void main(String[] args) {

        if (args.length < 3) {
            System.err.println("[-] Incorrect Argument number");
            System.exit(ERR_WRONG_ARGS);
        }

        try {

            srvc_port = Integer.parseInt(args[0]);
            mcast_addr = args[1];
            mcast_port = Integer.parseInt(args[2]);

            addr = InetAddress.getByName(mcast_addr);
            serverSocket = new DatagramSocket();
        } catch (Exception e) {

        }

        /**
         * Create threads
         */
        Timer t = new Timer();
        TimerTask adv_network = new TimerTask() {
            @Override
            public void run() {
                try {
                    String local_addr=InetAddress.getLocalHost().toString().split("/")[1];
                    String msg=String.format("Multicast Message:%s:%d",local_addr,srvc_port);
                    DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),msg.getBytes().length, addr, mcast_port);
                    serverSocket.send(msgPacket);
                    String status=String.format("multicast: <mcast_addr>%s <mcast_port>%d: <srvc_addr>%s <srvc_port>%d", mcast_addr,mcast_port,local_addr,srvc_port);
                    System.out.println(status);
                } catch (IOException ex) {
                    System.err.println("[-] Error sending multicast Message");
                    System.exit(ERR_SENDING_ADV);
                    
                }
            }
        };
        t.scheduleAtFixedRate(adv_network, 0, 1000);

    }
}
