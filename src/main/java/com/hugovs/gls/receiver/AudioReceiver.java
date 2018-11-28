package com.hugovs.gls.receiver;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

class AudioReceiver {

    private AudioReceiverThread task;
    private Queue<byte[]> samples;

    private DatagramSocket socket;
    private AudioInputStream stream;

    private int bufferSize = 1280;

    public AudioReceiver(int port, int bufferSize) {
        this.bufferSize = bufferSize;
        this.samples = new LinkedList<>();

        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

    }

    public void startReceiving() {

        System.out.println("GLS: Starting server ...");

        task = new AudioReceiverThread(samples, socket, bufferSize);
        task.start();

        System.out.println("GLS: Server started!");
    }

    public void stopReceiving() {
        task.interrupt();
    }

    public boolean isReceiving() {
        return !task.isInterrupted();
    }

    public Queue<byte[]> getSamples() {
        return samples;
    }

    private static class AudioReceiverThread extends Thread {

        private Queue<byte[]> samples;
        private DatagramSocket socket;
        private int bufferSize;

        AudioReceiverThread(Queue<byte[]> samples, DatagramSocket socket, int bufferSize) {
            this.samples = samples;
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
                    socket.receive(packet);
                    samples.add(packet.getData());
                } catch (IOException e) {
                    System.err.println("GLS: Failed to receive packet -> " + e.getMessage());
                }

            }

            System.out.println("GLS: Audio receiver stopped.");

        }
    }

}
