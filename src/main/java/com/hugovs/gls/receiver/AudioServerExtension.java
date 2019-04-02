package com.hugovs.gls.receiver;

/**
 * An extension to be registered to a audioServer.
 *
 * @author Hugo Sartori
 */
public abstract class AudioServerExtension {

    private AudioServer audioServer = null;

    public AudioServer getAudioServer() {
        return audioServer;
    }

    void setAudioServer(AudioServer audioServer) {
        this.audioServer = audioServer;
    }

    public void onServerStart() {}
    public void onServerClose() {}

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
