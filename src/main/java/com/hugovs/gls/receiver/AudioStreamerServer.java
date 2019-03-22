package com.hugovs.gls.receiver;

import com.hugovs.gls.receiver.listeners.SoundPlayer;
import com.hugovs.gls.receiver.listeners.ScheduledWavDumper;
import org.apache.log4j.Logger;

import javax.sound.sampled.AudioFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Facade of the audio stream.
 */
public class AudioStreamerServer {

    private final Logger log = Logger.getLogger(AudioStreamerServer.class);

    // Extensions
    private final Collection<Extension> extensions = new ArrayList<>();

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

        // Load extensions
        extensions.forEach(extension -> receiver.addListener(extension));
        extensions.forEach(extension -> {
            try {
                extension.onServerStart();
            } catch (Exception e) {
                log.error("Failed to start extension " + extension.getClass().getSimpleName(), e);
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

        // Unload extensions
        extensions.forEach(extension -> {
            try {
                extension.onServerClose();
            } catch (Exception e) {
                log.error("Failed to close extension " + extension.getClass().getSimpleName(), e);
            }
        });

    }

    /**
     * Add one or more server {@link Extension}s.
     * @param extensions: one or more {@link Extension} to be added.
     */
    public void addExtension(Extension ... extensions) {
        this.extensions.addAll(Arrays.asList(extensions));
    }

    /**
     * Stop the {@link AudioReceiver}.
     */
    public void stopReceiving() {
        receiver.stopReceiving();
        log.info("Server stopped.");
    }

    public abstract static class Extension implements AudioReceiver.Listener {

        @Override
        public void onDataReceived(byte[] data) {

        }

        public abstract void onServerStart();
        public abstract void onServerClose();

    }

}
