package me.reedknight;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * <h3>STOP AND WAIT WITH ARQ PROTOCOL RECEIVER</h3>
 * <p>Simulation of the functioning of a data-link layer
 * Stop And Wait Protocol with ARQ(Automatic Repeat Request)
 * operating in a <b>noisy channel</b> implemented using Java API's
 * DatagramSocket and DatagramPacket classes to communicated between
 * Sender and Receiver programs through UDP.</p>
 *
 * @author Shaswata
 * @created 8/26/16
 */
public class StopNWaitARQ_Receiver {

    private ArrayList<String> frame_list;
    private int expected_frame_index;

    StopNWaitARQ_Receiver() {
        frame_list = new ArrayList<String>(5);
        expected_frame_index = 0;
    }

    private void receiveFrames() {
        Runnable receiverThread = () -> {
            try {
                String frame = Network.receiveFrame(Settings.S_PORT);
                System.out.println("Received Frame : " + frame);
                if(verifyFrame(frame) == true) {
                    // Send ACK for expected frame and store frame
                    Network.sendFrameAck("ACK" + (expected_frame_index&1), Settings.R_PORT);
                    frame_list.add(frame.substring(3));
                    expected_frame_index += 1;
                } else {
                    // Send NAK for expected frame
                    Network.sendFrameAck("NAK" + (expected_frame_index&1), Settings.R_PORT);
                }
                displayReceivedFrames();
            } catch (SocketTimeoutException e) {
                // This is will happen since timeout will be 0
            }
        };

        System.out.println("Receiver Process Started\n========================\n");

        while(true) {
            Thread x = new Thread(receiverThread);
            x.start();
            try {
                x.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void displayReceivedFrames() {
        System.out.println("Received Frames : \n-----------------------");
        for(String packet : frame_list) {
            System.out.print(packet + ", ");
        }
        System.out.print("\b");
        System.out.println();
    }

    private boolean verifyFrame(String frame) {
        try {
            return (Integer.parseInt(frame.substring(1, 2)) == (expected_frame_index&1));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String args[]) {
        StopNWaitARQ_Receiver obj = new StopNWaitARQ_Receiver();
        obj.receiveFrames();
    }

}
