package l02;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerL02 {

    /**
     * Error codes
     */
    public static int ERR_WRONG_ARGS = 1;
    public static int ERR_SENDING_ADV = 2;
    public static int ERR_SETTING_ADV = 3;
    public static int ERR_SETTING_SOCK = 4;

    /**
     * Socket-related variables
     */
    private static DatagramSocket serverSocket;
    private static InetAddress addr;
    private static int srvc_port;
    private static String mcast_addr;
    private static int mcast_port;

    /**
     * Static Variables
     */
    private static int mcastRate ;

    public ServerL02(int srvcpt, String mcastAdress, int mcastPort,int rate) {
        srvc_port = srvcpt;
        mcast_addr = mcastAdress;
        mcast_port = mcastPort;
        mcastRate= rate;

        try {
            addr = InetAddress.getByName(mcast_addr);
            serverSocket = new DatagramSocket();
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServerL02.class.getName()).log(Level.SEVERE, null, ex);
            throwErrorandExit("[-] Error setting multicast Destination", ERR_SETTING_ADV);

        } catch (SocketException ex) {
            Logger.getLogger(ServerL02.class.getName()).log(Level.SEVERE, null, ex);
            throwErrorandExit("[-] Error setting multicast socket", ERR_SETTING_SOCK);
        }

    }

    public static void main(String[] args) {

        //For testing porpuses
        ServerL02 abc = new ServerL02(8080, "224.13.3.7", 1111,1000);
        abc.createMenance();

    }

    private static void throwErrorandExit(String msg, int error) {
        System.err.println(msg);
        System.exit(error);
    }

    public void createMenance() {
        /**
         * Create threads
         */
        Timer t = new Timer();
        TimerTask adv_network = new TimerTask() {
            @Override
            public void run() {
                try {
                    String local_addr = InetAddress.getLocalHost().toString().split("/")[1];
                    String msg = String.format("Multicast Message:%s:%d", local_addr, srvc_port);
                    DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, addr, mcast_port);
                    serverSocket.send(msgPacket);
                    String status = String.format("multicast: <mcast_addr>%s <mcast_port>%d: <srvc_addr>%s <srvc_port>%d", mcast_addr, mcast_port, local_addr, srvc_port);
                    System.out.println(status);
                } catch (IOException ex) {
                    System.err.println("[-] Error sending multicast Message");
                    System.exit(ERR_SENDING_ADV);

                }
            }
        };
        t.scheduleAtFixedRate(adv_network, 0, mcastRate);
    }
}
