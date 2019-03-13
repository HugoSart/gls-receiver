package com.hugovs.gls;

import com.hugovs.gls.receiver.AudioStreamerServer;

import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException {
        float factor = 1f;
        AudioStreamerServer server = new AudioStreamerServer((int)(16000 * factor), 16, 1280);
        server.startReceiving(55555);
    }

}
