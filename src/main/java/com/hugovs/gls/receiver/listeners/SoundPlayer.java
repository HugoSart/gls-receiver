package com.hugovs.gls.receiver.listeners;

import com.hugovs.gls.receiver.AudioReceiver;

import javax.sound.sampled.*;

/**
 * Listener to play the arrived samples on the default device's {@link SourceDataLine}.
 */
public class SoundPlayer implements AudioReceiver.Listener {

    private static final float VOLUME = 4.0206f;

    // Important
    private final SourceDataLine sourceDataLine;

    public SoundPlayer(AudioFormat format) {
        sourceDataLine = createSourceDataLine(format);
    }

    /**
     * Method called when a new data is received by the {@link AudioReceiver}.
     * Plays the byte as a sound on the default {@link SourceDataLine}.
     *
     * @param data the new data received.
     */
    @Override
    public void onDataReceived(byte[] data) {
        new Thread(() -> writeToDataLine(data)).start();
    }

    /**
     * Write a sample to the {@link SourceDataLine}.
     * @param data the data to be written.
     */
    private void writeToDataLine(byte[] data) {
        sourceDataLine.write(data, 0, data.length);
    }

    /**
     * Creates a {@link SourceDataLine} with pre-defined values.
     * @param format the {@link AudioFormat} of the {@link SourceDataLine} to be created.
     * @return  {@link SourceDataLine}  : if the line is available;
     *          {@code null}            : if don't.
     */
    private SourceDataLine createSourceDataLine(AudioFormat format) {
        try {
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(format);
            FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(VOLUME);
            sourceDataLine.start();
            return sourceDataLine;
        } catch (LineUnavailableException e) {
            return null;
        }
    }

}
