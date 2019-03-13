package com.hugovs.gls.receiver;

import javax.sound.sampled.*;

/**
 * The {@link AudioPlayer} process the samples and send to the default audio output.
 */
class AudioPlayer {

    private AudioPlayerThread task;
    private AudioInputStream stream;

    public AudioPlayer(AudioInputStream stream) {
        this.stream = stream;
    }

    /**
     * Starts the audio thread.
     */
    public void startPlaying() {
        task = new AudioPlayerThread(stream);
        task.start();
    }

    /**
     * Stops the audio thread.
     */
    public void stopPlaying() {
        task.interrupt();
    }

    /**
     * This class encapsulates the concurrent audio playing.
     */
    private static class AudioPlayerThread extends Thread {

        private AudioInputStream stream;

        AudioPlayerThread(AudioInputStream stream) {
            this.stream = stream;
        }

        @Override
        public void run() {

            try {

                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, stream.getFormat());
                SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

                sourceDataLine.open(stream.getFormat());

                FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(4.0206f);

                sourceDataLine.start();
                sourceDataLine.open(stream.getFormat());

                sourceDataLine.start();

                final byte[] buffer = new byte[(int)stream.getFrameLength()];
                int read = buffer.length;
                while (!interrupted()) {
                    if ((read = stream.read(buffer, 0, read)) > 0) {
                        // System.out.println("GLS: Playing " + buffer.length + " bytes from the input stream.");
                        sourceDataLine.write(buffer, 0, buffer.length);
                    }
                }

                sourceDataLine.drain();
                sourceDataLine.close();

            } catch (Exception e) {
                System.out.println("GLS: Speakers not working ...");
                e.printStackTrace();
            }

        }
    }

}
