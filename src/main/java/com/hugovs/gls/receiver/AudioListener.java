package com.hugovs.gls.receiver;

/**
 * Interface to act as callback on when data arrive.
 *
 * @author Hugo Sartori
 */
public interface AudioListener {

    /**
     * Process the received {@link AudioData}.
     *
     * @param data: the {@link AudioData} to be processed or {@code null} if no process should be done.
     */
    default void onDataReceived(AudioData data) {}

}
