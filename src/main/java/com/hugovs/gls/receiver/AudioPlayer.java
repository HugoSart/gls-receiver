package com.hugovs.gls.receiver;

import javax.sound.sampled.*;
import java.util.Queue;

class AudioPlayer {

    private AudioPlayerThread task;
    private Queue<byte[]> samples;

    private AudioFormat format;

    public AudioPlayer(Queue<byte[]> samples, AudioFormat format) {
        this.samples = samples;
        this.format = format;
    }

    public void startPlaying() {
        task = new AudioPlayerThread(samples, format);
        task.start();
    }

    public void stopPlaying() {
        task.interrupt();
    }

    private static class AudioPlayerThread extends Thread {

        private Queue<byte[]> samples;
        private AudioFormat format;

        AudioPlayerThread(Queue<byte[]> samples, AudioFormat format) {
            this.samples = samples;
            this.format = format;
        }

        @Override
        public void run() {

            try {

                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

                sourceDataLine.open(format);

                FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(4.0206f);

                sourceDataLine.start();
                sourceDataLine.open(format);

                sourceDataLine.start();

                while (!interrupted()) {
                    while (!samples.isEmpty()) {
                        byte[] sample = samples.poll();
                        sourceDataLine.write(sample, 0, sample.length);
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
