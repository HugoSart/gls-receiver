package com.hugovs.gls;

import com.hugovs.gls.extensions.WaveDrawer;
import com.hugovs.gls.receiver.AudioStreamerServer;

public class Application {

    public static void main(String[] args) {
        float factor = 1f;
        AudioStreamerServer server = new AudioStreamerServer((int)(44100 * factor), 16, (int)(1280 * factor));
        server.addExtension(new WaveDrawer());
        server.startReceiving(55555);
    }

}
