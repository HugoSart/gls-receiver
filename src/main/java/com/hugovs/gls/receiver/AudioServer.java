package com.hugovs.gls.receiver;

import org.apache.log4j.Logger;

import javax.sound.sampled.AudioFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Facade of the audio stream.
 *
 * @author Hugo Sartori
 */
public class AudioServer {

    private final Logger log = Logger.getLogger(AudioServer.class);

    // Extensions
    private final Collection<AudioServerExtension> audioServerExtensions = new ArrayList<>();

    // Properties
    private final int bufferSize; // 1280

    // References
    private AudioReceiver receiver;
    private AudioFormat audioFormat;

    public AudioServer(int sampleRate, int sampleSize, int bufferSize) {
        this.audioFormat = new AudioFormat(sampleRate, sampleSize, 1, true, false);
        this.bufferSize = bufferSize;
    }

    /**
     * Starts a {@link AudioReceiver} on the given port.
     *
     * @param port the port to listen to.
     */
    public void startReceiving(int port) {

        log.info("Starting server ...");

        // Creates the AudioReceiver and also the listeners
        receiver = new AudioReceiver(port, bufferSize, 16);

        // Load audioServerExtensions
        audioServerExtensions.forEach(audioServerExtension -> {
            if (DataListener.class.isAssignableFrom(audioServerExtension.getClass()))
                receiver.addListener((DataListener) audioServerExtension);
            if (DataFilter.class.isAssignableFrom(audioServerExtension.getClass()))
                receiver.addFilter((DataFilter) audioServerExtension);
        });
        audioServerExtensions.forEach(audioServerExtension -> {
            try {
                audioServerExtension.setAudioServer(this);
                audioServerExtension.onServerStart();
            } catch (Exception e) {
                log.error("Failed to start audioServerExtension " + audioServerExtension.getClass().getSimpleName(), e);
            }
        });

        receiver.startReceiving();

        while (!receiver.isReceiving()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        // Unload audioServerExtensions
        audioServerExtensions.forEach(audioServerExtension -> {
            try {
                audioServerExtension.onServerClose();
            } catch (Exception e) {
                log.error("Failed to close audioServerExtension " + audioServerExtension.getClass().getSimpleName(), e);
            }
        });

    }

    /**
     * Add one or more server {@link AudioServerExtension}s.
     *
     * @param audioServerExtensions: one or more {@link AudioServerExtension} to be added.
     */
    public void addExtension(AudioServerExtension... audioServerExtensions) {
        this.audioServerExtensions.addAll(Arrays.asList(audioServerExtensions));
    }

    /**
     * Add a {@link Collection} of {@link AudioServerExtension}s.
     *
     * @param audioServerExtensions: a {@link Collection} of {@link AudioServerExtension} to be added.
     */
    public void addExtension(Collection<AudioServerExtension> audioServerExtensions) {
        this.audioServerExtensions.addAll(audioServerExtensions);
    }

    /**
     * Stops the {@link AudioReceiver}.
     */
    public void stopReceiving() {
        receiver.stopReceiving();
        log.info("AudioServer stopped.");
    }



}
