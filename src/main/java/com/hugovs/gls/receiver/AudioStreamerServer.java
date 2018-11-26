package com.hugovs.gls.receiver;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class AudioStreamerServer {

    private DatagramSocket socket;
    private AudioInputStream stream;
    private AudioFormat format;

    private int sampleRate = 16000;
    private int sampleSize = 16;
    private int bufferSize = 1280;

    public AudioStreamerServer() {
        format = new AudioFormat(sampleRate, sampleSize, 1, true, false);
    }

    public void start(int port) {

        System.out.println("GLS: Starting server ...");

        try {

            socket = new DatagramSocket(port);
            byte[] receive = new byte[bufferSize];

            System.out.println("GLS: Server started!");

            while (true) {
                System.out.println("GLS: Waiting for packet ...");

                final DatagramPacket packet = new DatagramPacket(receive, receive.length);
                socket.receive(packet);

                ByteArrayInputStream bytes = new ByteArrayInputStream(packet.getData());
                stream = new AudioInputStream(bytes, format, packet.getLength());

                new Thread(new Runnable() {
                    public void run() {
                        toSpeaker(packet.getData());
                    }
                }).start();

                System.out.println("GLS: Packet received!");
            }

        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void stop() {

        if (socket.isClosed())
            throw new RuntimeException("Attempt to stop inexistent socket connection");

        socket.close();
    }

    private void toSpeaker(byte soundbytes[]) {
        try {

            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

            sourceDataLine.open(format);

            FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(6.0206f);

            sourceDataLine.start();
            sourceDataLine.open(format);

            sourceDataLine.start();

            //System.out.println("format? :" + sourceDataLine.getFormat());

            sourceDataLine.write(soundbytes, 0, soundbytes.length);
            sourceDataLine.drain();
            sourceDataLine.close();
        } catch (Exception e) {
            System.out.println("Not working in speakers...");
            e.printStackTrace();
        }
    }

}
