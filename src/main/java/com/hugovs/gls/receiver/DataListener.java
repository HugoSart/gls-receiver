package com.hugovs.gls.receiver;

/**
 * Interface to act as callback on when data arrive.
 *
 * @author Hugo Sartori
 */
public interface DataListener {
    default void onDataReceived(AudioData data) {}
}
