package com.hugovs.gls.receiver.api;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class GunshotAPI extends WebSocketServer {

    private static final Logger log = Logger.getLogger(GunshotAPI.class);

    private final Set<Device> devices = new HashSet<>();
    private final Gson gson = new Gson();

    public GunshotAPI(int port) {
        super(new InetSocketAddress(port));
    }

    public GunshotAPI(final InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        try {
            log.info("Connection STARTED with " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
        } catch (Exception e) {
            log.error("Failed to handle onoOpen", e);
        }
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        try {
            if (webSocket != null) {
                log.info("Connection CLOSE on " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
            } else {
                log.info("Connection CLOSE");
            }
        } catch (Exception e) {
            log.error("Failed to handle onClose", e);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        log.debug("onMessage from " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + ": " + s);
        try {
            final Message message = gson.fromJson(s, Message.class);
            log.info("Message received from " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + " to " + message.destination);

            final String[] parts = message.destination.split("[.]");
            if (parts[0].equals("device")) {
                switch (parts[1]) {
                    case "add": {
                        final Device device = gson.fromJson(message.body, Device.class);
                        devices.add(device);
                        log.info("Added new device: " + device);
                        break;
                    }
                    case "update": {
                        final Device device = gson.fromJson(message.body, Device.class);
                        devices.add(device);
                        log.info("Updated device: " + device);
                        break;
                    }
                    case "delete": {
                        final Device device = gson.fromJson(message.body, Device.class);
                        devices.remove(device);
                        log.info("Deleted device: " + device);
                        break;
                    }
                    case "fetch": {
                        webSocket.send(gson.toJson(new Message("response/device.fetch", gson.toJson(devices))));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to handle onMessage", e);
        }

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        try {
            if (webSocket != null) {
                log.error("Connection ERROR on " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress(), e);
            } else {
                log.error("Connection ERROR", e);
            }
        } catch (Exception e2) {
            log.error("Failed to handle onError", e2);
        }
    }

    @Override
    public void onStart() {
        log.info("GunshotAPI started on " + getPort());
    }

    public void sendFrequencies(long deviceId, final double[] ft) {
        final Message message = new Message("topic/device.frequency", gson.toJson(new Frequency(deviceId, ft)));
        broadcast(gson.toJson(message));
    }

    private static class Message implements Serializable {
        String destination;
        String body;

        Message(String destination, String body) {
            this.destination = destination;
            this.body = body;
        }

        @Override
        public String toString() {
            return "Message<destination=" + destination + ", body=" + body + ">";
        }
    }

    private static class Device implements Serializable {
        long id;
        double latitude, longitude;

        Device(long id, double latitude, double longitude) {
            this.id = id;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public boolean equals(Object obj) {
            return obj.equals(id);
        }


        @Override
        public int hashCode() {
            return Long.hashCode(id);
        }

        @Override
        public String toString() {
            return "Device<id=" + id + ", latitude=" + latitude + ", longitude=" + longitude + ">";
        }
    }

    private static class Frequency implements Serializable {
        long deviceId;
        double[] values;

        Frequency(long deviceId, double[] values) {
            this.deviceId = deviceId;
            this.values = values;
        }

    }

}
