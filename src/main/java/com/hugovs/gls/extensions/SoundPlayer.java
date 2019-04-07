package com.hugovs.gls.extensions;

import com.hugovs.gls.receiver.AudioData;
import com.hugovs.gls.receiver.AudioListener;
import com.hugovs.gls.receiver.AudioReceiver;
import com.hugovs.gls.receiver.AudioServerExtension;

import javax.sound.sampled.*;

/**
 * AudioServerExtension to play the arrived samples on the default device's {@link SourceDataLine}.
 *
 * @author Hugo Sartori
 */
public class SoundPlayer extends AudioServerExtension implements AudioListener {

    private static final float VOLUME = 4.0206f;

    // Important
    private SourceDataLine sourceDataLine;


    /**
     * Method called when the server is started.
     * Initialize the default {@link SourceDataLine}.
     */
    @Override
    public void onServerStart() {
        sourceDataLine = createSourceDataLine(getAudioServer().getAudioFormat());
    }

    /**
     * Method called when the server is closed.
     * Closes the default {@link SourceDataLine}.
     */
    @Override
    public void onServerClose() {
        sourceDataLine.close();
    }

    /**
     * Method called when a new data is received by the {@link AudioReceiver}.
     * Plays the byte as a sound on the default {@link SourceDataLine}.
     *
     * @param audioData the new data received.
     */
    @Override
    public void onDataReceived(AudioData audioData) {
        new Thread(() -> writeToDataLine(audioData.getSamples())).start();
    }

    /**
     * Write a sample to the {@link SourceDataLine}.
     *
     * @param data the data to be written.
     */
    private void writeToDataLine(byte[] data) {
        sourceDataLine.write(data, 0, data.length);
    }

    /**
     * Creates a {@link SourceDataLine} with pre-defined values.
     *
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
