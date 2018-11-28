package com.hugovs.gls.receiver;

import javax.sound.sampled.*;

public class AudioStreamerServer {

    private AudioPlayer player;
    private AudioReceiver receiver;

    private int sampleRate = 16000;
    private int sampleSize = 16;
    private int bufferSize = 1280;

    public AudioStreamerServer(int sampleRate, int sampleSize, int bufferSize) {
        this.sampleRate = sampleRate;
        this.sampleSize = sampleSize;
        this.bufferSize = bufferSize;
    }

    public void startReceiving(int port) {

        System.out.println("GLS: Starting server ...");

        receiver = new AudioReceiver(port, bufferSize);
        player = new AudioPlayer(receiver.getSamples(), new AudioFormat(sampleRate, sampleSize, 1, true, false));

        receiver.startReceiving();
        while (!receiver.isReceiving());
        player.startPlaying();
        while (receiver.isReceiving());

    }

    public void stopReceiving() {
        player.stopPlaying();
        receiver.stopReceiving();
    }

}
