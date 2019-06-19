package com.hugovs.gls.receiver.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hugovs.gls.receiver.api.model.Device;
import com.hugovs.gls.receiver.api.model.Frequency;
import com.hugovs.gls.receiver.api.model.Gunshot;
import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;

/**
 * WebSocket API to send GLS data to the world.
 *
 * @author Hugo Sartori
 */
public class GunshotAPI extends WebSocketServer {

    private static final Logger log = Logger.getLogger(GunshotAPI.class);

    private DeviceSet devices = new DeviceSet();
    private Gson gson = new Gson();
    private FileReader dbReader;
    private FileWriter dbWriter;


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
    public void onClose(WebSocket webSocket, int code, String reason, boolean b) {
        try {
            log.info("Connection CLOSE: " + code);
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
                        updateDatabase();
                        break;
                    }
                    case "update": {
                        final Device device = gson.fromJson(message.body, Device.class);
                        devices.add(device);
                        log.info("Updated device: " + device);
                        updateDatabase();
                        break;
                    }
                    case "delete": {
                        final Device device = gson.fromJson(message.body, Device.class);
                        devices.remove(device);
                        log.info("Deleted device: " + device);
                        updateDatabase();
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
        loadDatabase();
        log.info("GunshotAPI started on " + getPort());
    }

    /**
     * Send a frequency register on broadcast.
     *
     * @param deviceId: the device's id that refers to the extracted frequencies.
     * @param ft:       the data.
     */
    public void sendFrequencies(long deviceId, final double[] ft) {
        final Message message = new Message("topic/device.frequency", gson.toJson(new Frequency(deviceId, ft)));
        broadcast(gson.toJson(message));
    }

    /**
     * Send a gunshot register on broadcast.
     *
     * @param deviceId:  the device's id that refers to the detected gunshot.
     * @param timestamp: the timestamp that the gunshot was firstly captured.
     */
    public void sendGunshot(long deviceId, long timestamp) {
        final Message message = new Message("topic/device.gunshot", gson.toJson(new Gunshot(deviceId, timestamp)));
        broadcast(gson.toJson(message));
    }

    /**
     * Load the database from the database.json file.
     */
    private void loadDatabase() {
        try {
            dbReader = new FileReader("database.json");
            devices = gson.fromJson(dbReader, DeviceSet.class);
            if (devices == null) devices = new DeviceSet();
        } catch (IOException | JsonSyntaxException e) {
            log.error("Failed to load database", e);
            devices = new DeviceSet();
        }
    }

    /**
     * Update the data in the database.json file.
     */
    private void updateDatabase() {

        try {
            dbWriter = new FileWriter("database.json");
            gson.toJson(devices, dbWriter);
            dbWriter.flush();
            dbWriter.close();
            log.info("Database updated");
        } catch (IOException e) {
            log.error("Failed to update database", e);
        }
    }

    private void closeDatabase() {
        try {
            dbWriter.close();
            dbReader.close();
        } catch (IOException e) {
            log.error("Failed to close database", e);
        }
    }

    /**
     * A class to encapsulate WebSocket messages in and out.
     */
    public static class Message implements Serializable {
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

    private static class DeviceSet extends HashSet<Device> implements Serializable {
        DeviceSet() { }
        DeviceSet(Collection c) { super(c); }
        DeviceSet(int initialCapacity, float loadFactor) { super(initialCapacity, loadFactor); }
        DeviceSet(int initialCapacity) { super(initialCapacity); }
    }

}
