package com.hugovs.gls.receiver;

/**
 * Interface to provide audio data filtering.
 *
 * @author Hugo Sartori
 */
public interface DataFilter {
    default byte[] filter(byte[] data) { return data; }
}