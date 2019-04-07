package com.hugovs.gls.receiver;

import org.apache.log4j.Logger;

import java.net.DatagramPacket;
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
    private final AudioInput audioInput;
    private final List<AudioListener> audioListeners = new ArrayList<>();
    private final List<AudioFilter> audioFilters = new ArrayList<>();

    // Thread
    private AudioReceiverThread task;

    public AudioReceiver(AudioInput audioInput) {
        this.audioInput = audioInput;
    }

    /**
     * Starts to listen for packets on the specified port.
     */
    public void startReceiving() {

        log.info("Start receiving ...");

        task = new AudioReceiverThread(audioInput);
        task.audioListeners = audioListeners;
        task.audioFilters = audioFilters;
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
     *
     * @return {@code true} if the receiver are online;
     * {@code false} if not.
     */
    public boolean isReceiving() {
        return !task.isInterrupted();
    }

    /**
     * Adds a new audioListener.
     *
     * @param audioListener the audioListener to be added.
     */
    public void addListener(AudioListener audioListener) {
        this.audioListeners.add(audioListener);
    }

    /**
     * Removes an existing audioListener.
     *
     * @param audioListener the audioListener to be removed.
     */
    public void removeListener(AudioListener audioListener) {
        this.audioListeners.remove(audioListener);
    }

    /**
     * Adds a new audioFilter.
     *
     * @param audioFilter the listener to be added.
     */
    public void addFilter(AudioFilter audioFilter) {
        this.audioFilters.add(audioFilter);
    }

    /**
     * Removes an existing audioFilter.
     *
     * @param audioFilter the listener to be removed.
     */
    public void removeFilter(AudioFilter audioFilter) {
        this.audioFilters.remove(audioFilter);
    }

    /**
     * This class encapsulates the concurrent packet listening.
     */
    private static class AudioReceiverThread extends Thread {

        private Logger log = Logger.getLogger(AudioReceiverThread.class);

        private List<AudioListener> audioListeners;
        private List<AudioFilter> audioFilters;
        private AudioInput audioInput;

        AudioReceiverThread(AudioInput audioInput) {
            this.audioInput = audioInput;
        }

        @Override
        public void run() {

            log.info("Audio Received thread is running");

            while (!interrupted()) {

                if (audioInput == null) continue;

                AudioData data = audioInput.read();
                if (data == null) continue;

                // Apply audioFilters in insertion order
                for (AudioFilter audioFilter : audioFilters)
                    data = audioFilter.filter(data);

                // Call audio listeners
                for (AudioListener audioListener : audioListeners)
                    audioListener.onDataReceived(data);

            }


        }
    }

}
