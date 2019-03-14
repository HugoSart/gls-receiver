package com.hugovs.gls.receiver;

import com.hugovs.gls.receiver.listeners.SoundPlayer;
import com.hugovs.gls.receiver.listeners.ScheduledWavDumper;
import org.apache.log4j.Logger;

import javax.sound.sampled.AudioFormat;
import java.util.concurrent.TimeUnit;

/**
 * Facade of the audio stream.
 */
public class AudioStreamerServer {

    private final Logger log = Logger.getLogger(AudioStreamerServer.class);

    // Properties
    private final int bufferSize; // 1280

    // References
    private AudioReceiver receiver;
    private AudioFormat audioFormat;

    public AudioStreamerServer(int sampleRate, int sampleSize, int bufferSize) {
        this.audioFormat = new AudioFormat(sampleRate, sampleSize, 1, true, false);
        this.bufferSize = bufferSize;
    }

    /**
     * Starts a {@link AudioReceiver} on the given port.
     * @param port the port to listen to.
     */
    public void startReceiving(int port) {

        log.info("Starting server ...");

        // Creates the AudioReceiver and also the listeners
        receiver = new AudioReceiver(port, bufferSize);
        receiver.addListener(new ScheduledWavDumper(audioFormat));
        receiver.addListener(new SoundPlayer(audioFormat));
        receiver.startReceiving();

        while (!receiver.isReceiving()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                // ignore
            }
        }

    }

    /**
     * Stop the {@link AudioReceiver}.
     */
    public void stopReceiving() {
        receiver.stopReceiving();
        log.info("Server stopped.");
    }

}
