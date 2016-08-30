package me.reedknight;

import java.io.IOException;
import java.net.*;
import java.util.Set;

/**
 * Created by reedknight on 8/30/16.
 */
public class Network {

    private static DatagramSocket ds_sender_1, ds_sender_2, ds_receiver_1, ds_receiver_2 = null;

    /**
     * Send the frame
     */
    public static void sendFrame(String frame, int port) {
        sender(ds_sender_1, port, frame);
    }

    public static void sendFrameAck(String frame, int port) {
        sender(ds_sender_2, port, frame);
    }

    public static String receiveFrameAck(int port, int timeout) throws SocketTimeoutException {
        return receiver(ds_sender_2, port, timeout);
    }

    public static String receiveFrame(int port) throws SocketTimeoutException {
        return receiver(ds_receiver_2, port, 0);
    }

    private static void sender(DatagramSocket ds, int port, String frame) {
        if(ds != null)
            ds.close();
        ds = null;
        try {
            ds = new DatagramSocket();
            InetAddress ip = InetAddress.getByName(Settings.IP_ADDRESS);
            DatagramPacket dp = new DatagramPacket(frame.getBytes(), frame.length(), ip, port);
            ds.send(dp);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(ds != null)
                ds.close();
        }
    }

    private static String receiver(DatagramSocket ds, int port, int timeout) throws SocketTimeoutException{
        if(ds != null)
            ds.close();
        ds = null;
        String str = null;
        try {
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            ds.setSoTimeout(timeout);
            byte[] buf = new byte[1024];
            DatagramPacket dp = new DatagramPacket(buf, 1024);
            ds.receive(dp);
            str = new String(dp.getData(), 0, dp.getLength());
        } catch(SocketTimeoutException e) {
            throw new SocketTimeoutException("");
        } catch (BindException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(ds != null)
                ds.close();
        }
        return str;
    }
}
