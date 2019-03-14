package com.hugovs.gls.receiver;

import com.hugovs.gls.util.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link AudioReceiver} receive {@link DatagramPacket}s sent to the given port.
 */
public class AudioReceiver {

    private final Logger log = Logger.getLogger(AudioReceiver.class);

    // Utils
    private final List<Listener> listeners = new ArrayList<>();
    private final DatagramSocket socket;
    private final int bufferSize;

    // Thread
    private AudioReceiverThread task;

    public AudioReceiver(int port, int bufferSize) {
        this.bufferSize = bufferSize;

        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Starts to listen for packets on the specified port.
     */
    public void startReceiving() {

        log.info("Start receiving ...");

        task = new AudioReceiverThread(socket, bufferSize);
        task.listeners = listeners;
        task.start();

    }

    /**
     * Stops the listening.
     */
    public void stopReceiving() {
        task.interrupt();
    }

    /**
     * Checks if the {@link DatagramPacket} are being received.
     * @return  {@code true} if the receiver are online;
     *          {@code false} if not.
     */
    public boolean isReceiving() {
        return !task.isInterrupted();
    }

    /**
     * Adds a new listener.
     * @param listener the listener to be added.
     */
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a existing listener.
     * @param listener the listener to be removed.
     */
    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    /**
     * This class encapsulates the concurrent packet listening.
     */
    private static class AudioReceiverThread extends Thread {

        private Logger log = Logger.getLogger(AudioReceiverThread.class);

        private DatagramSocket socket;
        private int bufferSize;
        private List<Listener> listeners;

        AudioReceiverThread(DatagramSocket socket, int bufferSize) {
            this.socket = socket;
            this.bufferSize = bufferSize;
        }

        @Override
        public void run() {

            byte[] receive = new byte[bufferSize];

            log.info("Audio Received thread is running");

            while (!interrupted()) {

                final DatagramPacket packet = new DatagramPacket(receive, receive.length);

                try {
                    // System.out.println("GLS: Waiting for packets ...");
                    socket.receive(packet);
                    byte[] data = packet.getData();
                    log.debug("Received " + packet.getData().length + " bytes: " + StringUtils.from(packet.getData()));
                    for (Listener listener : listeners)
                        listener.onDataReceived(data);
                } catch (IOException e) {
                    log.error("Failed to receive packet: ", e);
                }

            }



        }
    }

    /**
     * Interface to act as callback on when data arrive.
     */
    public interface Listener {
        void onDataReceived(byte[] data);
    }

}
