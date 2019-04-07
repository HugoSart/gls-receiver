package com.hugovs.gls.receiver;

/**
 * Interface to provide audio data filtering.
 *
 * @author Hugo Sartori
 */
public interface AudioFilter {

    /**
     *  Filter the data and pass it to the audio server pipeline.
     *  I. e., it can be used to remove audio noise.
     *
     * @param data: the {@link AudioData} to be filtered.
     * @return the filtered {@link AudioData}.
     */
    default AudioData filter(AudioData data) { return data; }

}