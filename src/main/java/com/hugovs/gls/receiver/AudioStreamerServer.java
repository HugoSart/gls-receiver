package com.hugovs.gls.receiver;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;

/**
 * This is the Facade of the audio stream.
 */
public class AudioStreamerServer {

    // Properties
    private int sampleRate = 16000;
    private int sampleSize = 16;
    private int bufferSize = 3584;

    // References
    private AudioPlayer player;
    private AudioReceiver receiver;
    private AudioFormat audioFormat;

    public AudioStreamerServer(int sampleRate, int sampleSize, int bufferSize) {
        this.sampleRate = sampleRate;
        this.sampleSize = sampleSize;
        this.bufferSize = bufferSize;
        this.audioFormat = new AudioFormat(sampleRate, sampleSize, 1, true, false);
    }

    /**
     * Starts a {@link AudioReceiver} on the given port and plays the audio using the {@link AudioPlayer}.
     * @param port the port to listen to.
     */
    public void startReceiving(int port) throws IOException {

        System.out.println("GLS: Starting server ...");

        receiver = new AudioReceiver(port, bufferSize);
        final AudioInputStream audioInputStream = new AudioInputStream(receiver.getInputStream(), audioFormat, bufferSize);
        player = new AudioPlayer(audioInputStream);

        receiver.startReceiving();
        while (!receiver.isReceiving());
        player.startPlaying();
        while (receiver.isReceiving());

    }

    /**
     * Stop the {@link AudioReceiver} and the {@link AudioPlayer}.
     */
    public void stopReceiving() {
        player.stopPlaying();
        receiver.stopReceiving();
    }

}
