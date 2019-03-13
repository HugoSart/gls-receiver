package com.hugovs.gls.receiver;

import com.hugovs.gls.StringUtils;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * The {@link AudioReceiver} receive {@link DatagramPacket}s sent to the given port.
 */
class AudioReceiver {

    private AudioReceiverThread task;
    private DatagramSocket socket;
    private int bufferSize = 1280;

    // Streams
    private final PipedOutputStream outputStream;
    private final PipedInputStream inputStream;

    public AudioReceiver(int port, int bufferSize) throws IOException {
        this.bufferSize = bufferSize;
        this.outputStream = new PipedOutputStream();
        this.inputStream = new PipedInputStream();
        this.outputStream.connect(inputStream);

        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Start to listen for packets on the specified port.
     */
    public void startReceiving() {

        System.out.println("GLS: Starting receiving ...");

        task = new AudioReceiverThread(outputStream, socket, bufferSize);
        task.start();

        System.out.println("GLS: Server receiving ifconfigstarted!");
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
     * Return the audio streaming output.
     * @return the out audio stream {@link OutputStream}.
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * This class encapsulates the concurrent packet listening.
     */
    private static class AudioReceiverThread extends Thread {

        private DatagramSocket socket;
        private OutputStream outStream;
        private int bufferSize;

        AudioReceiverThread(OutputStream outStream, DatagramSocket socket, int bufferSize) {
            this.outStream = outStream;
            this.socket = socket;
            this.bufferSize = bufferSize;
        }

        @Override
        public void run() {

            byte[] receive = new byte[bufferSize];

            System.out.println("GLS: Audio receiver started!");

            while (!interrupted()) {

                final DatagramPacket packet = new DatagramPacket(receive, receive.length);

                try {
                    // System.out.println("GLS: Waiting for packets ...");
                    System.out.println("GLS: Step 1 - Waiting packet");
                    socket.receive(packet);
                    System.out.println("GLS: Received " + packet.getData().length + " bytes: " + StringUtils.from(packet.getData()));
                    System.out.println("GLS: Step 2 - Writing to " + outStream);

                    // TODO: Stuck here after 3 packets
                    outStream.write(packet.getData());

                    System.out.println("GLS: Step 3 - Rerun");
                } catch (IOException e) {
                    System.err.println("GLS: Failed to receive packet -> " + e.getMessage());
                }

            }

            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("GLS: Failed to close OutputStream -> " + e);
            }

            System.out.println("GLS: Audio receiver stopped.");

        }
    }

}
