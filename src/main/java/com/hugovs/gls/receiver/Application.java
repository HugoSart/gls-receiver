package com.hugovs.gls.receiver;

public class Application {

    public static void main(String[] args) {
        AudioStreamerServer server = new AudioStreamerServer();
        server.start(12345);
    }

}
