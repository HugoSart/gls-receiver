package com.hugovs.gls.receiver;

import org.apache.log4j.Logger;

import javax.sound.sampled.AudioFormat;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Facade of the audio stream.
 *
 * @author Hugo Sartori
 */
public class AudioServer implements Closeable {

    private final Logger log = Logger.getLogger(AudioServer.class);

    // Extensions
    private final Collection<AudioServerExtension> audioServerExtensions = new ArrayList<>();

    // References
    private AudioInput input;
    private AudioReceiver receiver;
    private AudioFormat audioFormat;

    public AudioServer(int sampleRate, int sampleSize) {
        this.audioFormat = new AudioFormat(sampleRate, sampleSize, 1, true, false);
    }

    /**
     * Starts a {@link AudioReceiver}.
     */
    public void start() {

        log.info("Starting server ...");

        // Creates the AudioReceiver and also the listeners
        log.info("Audio input: " + input.getClass().getSimpleName());
        receiver = new AudioReceiver(input);

        // Load audioServerExtensions
        audioServerExtensions.forEach(audioServerExtension -> {
            if (AudioListener.class.isAssignableFrom(audioServerExtension.getClass()))
                receiver.addListener((AudioListener) audioServerExtension);
            if (AudioFilter.class.isAssignableFrom(audioServerExtension.getClass()))
                receiver.addFilter((AudioFilter) audioServerExtension);
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
     * Set the default {@link AudioInput} to the given one.
     *
     * @param input: the {@link AudioInput} to be used as input reader.
     */
    public void setInput(AudioInput input) {
        this.input = input;
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
     * Closes the {@link AudioReceiver}.
     */
    @Override
    public void close() throws IOException {
        receiver.stopReceiving();
        log.info("AudioServer stopped.");
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }


}
