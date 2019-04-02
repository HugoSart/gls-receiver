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
 *
 * @author Hugo Sartori
 */
public class AudioReceiver {

    private final Logger log = Logger.getLogger(AudioReceiver.class);

    // Listeners
    private final List<DataListener> dataListeners = new ArrayList<>();
    private final List<DataFilter> dataFilters = new ArrayList<>();

    // Utils
    private final DatagramSocket socket;
    private final int bufferSize;
    private final int metadataSize;

    // Thread
    private AudioReceiverThread task;

    public AudioReceiver(int port, int bufferSize, int metadataSize) {
        this.bufferSize = bufferSize;
        this.metadataSize = metadataSize;

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

        task = new AudioReceiverThread(socket, bufferSize, metadataSize);
        task.dataListeners = dataListeners;
        task.dataFilters = dataFilters;
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
     * Adds a new dataListener.
     *
     * @param dataListener the dataListener to be added.
     */
    public void addListener(DataListener dataListener) {
        this.dataListeners.add(dataListener);
    }

    /**
     * Removes an existing dataListener.
     *
     * @param dataListener the dataListener to be removed.
     */
    public void removeListener(DataListener dataListener) {
        this.dataListeners.remove(dataListener);
    }

    /**
     * Adds a new dataFilter.
     * @param dataFilter the listener to be added.
     */
    public void addFilter(DataFilter dataFilter) {
        this.dataFilters.add(dataFilter);
    }

    /**
     * Removes an existing dataFilter.
     * @param dataFilter the listener to be removed.
     */
    public void removeFilter(DataFilter dataFilter) {
        this.dataFilters.remove(dataFilter);
    }

    /**
     * This class encapsulates the concurrent packet listening.
     */
    private static class AudioReceiverThread extends Thread {

        private Logger log = Logger.getLogger(AudioReceiverThread.class);

        private DatagramSocket socket;
        private int bufferSize;
        private int metadataSize;
        private int totalBufferSize;
        private List<DataListener> dataListeners;
        private List<DataFilter> dataFilters;

        AudioReceiverThread(DatagramSocket socket, int bufferSize, int metadataSize) {
            this.socket = socket;
            this.bufferSize = bufferSize;
            this.metadataSize = metadataSize;
            this.totalBufferSize = bufferSize + metadataSize;
        }

        @Override
        public void run() {

            byte[] receive = new byte[totalBufferSize];

            log.info("Audio Received thread is running");

            while (!interrupted()) {

                final DatagramPacket packet = new DatagramPacket(receive, receive.length);

                try {
                    // System.out.println("GLS: Waiting for packets ...");
                    socket.receive(packet);
                    byte[] data = packet.getData();

                    // Apply dataFilters in insertion order
                    for (DataFilter dataFilter : dataFilters)
                        data = dataFilter.filter(data);

                    log.debug("Received " + packet.getData().length + " bytes: " + StringUtils.from(packet.getData()));
                    for (DataListener dataListener : dataListeners)
                        dataListener.onDataReceived(AudioData.wrap(data));

                } catch (IOException e) {
                    log.error("Failed to receive packet: ", e);
                }

            }



        }
    }

}
