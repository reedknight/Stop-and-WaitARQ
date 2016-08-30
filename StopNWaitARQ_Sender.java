package me.reedknight;

import java.net.SocketTimeoutException;

/**
 * <h3>STOP AND WAIT WITH ARQ PROTOCOL SENDER</h3>
 * <p>Simulation of the functioning of a data-link layer
 * Stop And Wait Protocol with ARQ(Automatic Repeat Request)
 * operating in a <b>noisy channel</b> implemented using Java API's
 * DatagramSocket and DatagramPacket classes to communicated between
 * Sender and Receiver programs through UDP.</p>
 *
 * @author Shaswata
 * @created 8/26/16
 */
public class StopNWaitARQ_Sender {

    String packets[] = {};
    String current_frame;
    int current_frame_no;
    String current_frame_acknowledgement_status;

    /**
     * Event to prepare and send message as frame
     */
    private void eventRequestSend() {
        Runnable task = () -> {
            Network.sendFrame(current_frame, Settings.S_PORT);
            System.out.println("Send Frame : " + current_frame);
            System.out.println("Wait for Acknowledgement");
            try {
                current_frame_acknowledgement_status = Network.receiveFrameAck(Settings.R_PORT, Settings.TIME_OUT);
            } catch (SocketTimeoutException e) {
                current_frame_acknowledgement_status = "NAK" + current_frame_no;
                System.out.println("Time Out While Sending Frame #" + current_frame_no);
            }
            System.out.println("ACK Status of Frame #" + current_frame_no + ": " + current_frame_acknowledgement_status);
        };

        while(true) {
            Thread x = new Thread(task);
            x.start();
            try {
                x.join();
                if(x.isAlive()) x.interrupt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(current_frame_acknowledgement_status.equals("ACK" + current_frame_no)) {
                break;
            }
        }
    }

    public void sendPackets() {
        Runnable senderThread = () -> {
            eventRequestSend();
        };

        System.out.println("Sender Process Started\n========================\n");

        for(int index = 0; index < packets.length; index++) {
            makeAndStoreFrame(packets[index], index);
            Thread s = new Thread(senderThread);
            s.start();
            try {
                s.join();
                if(s.isAlive()) s.interrupt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void makeAndStoreFrame(String data, int frame_no) {
        current_frame_no = frame_no & 1;
        current_frame = "$" + current_frame_no + "$" + data;
    }

    public void setPackets(String packets[]) {
        this.packets = packets;
    }


    public static void main(String args[]) {
        String packets[] = {"MSG 1", "MSG 2", "MSG 3", "MSG 4"};
        StopNWaitARQ_Sender obj = new StopNWaitARQ_Sender();
        obj.setPackets(packets);
        obj.sendPackets();
    }
}