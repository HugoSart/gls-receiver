package com.hugovs.gls.receiver;

/**
 * Interface for the audio input.
 * The {@link AudioReceiver} uses this interface to read data.
 *
 * @author Hugo Sartori
 */
public interface AudioInput {

    /**
     * Read a single {@link AudioData} synchronously.
     * This method should block the thread until an {@link AudioData} arrives.
     *
     * @return the {@link AudioData}.
     */
    default AudioData read() { return null; }

}
