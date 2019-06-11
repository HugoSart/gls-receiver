package com.hugovs.gls.receiver.api;

import com.hugovs.gls.receiver.api.model.Frequency;
import com.hugovs.gls.receiver.api.model.Gunshot;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Singleton manager to easily use the {@link GunshotAPI} service.
 *
 * @author Hugo Sartori
 */
public class GunshotAPIManager {

    private static final Logger log = Logger.getLogger(GunshotAPIManager.class);

    private static GunshotAPI api;

    private GunshotAPIManager() {
        //no instance
    }

    /**
     * Start the {@link GunshotAPI} connection.
     *
     * @param host: the host of the server.
     * @param port: the port to be listened.
     */
    public static void start(final String host, final int port) {
        api = new GunshotAPI(new InetSocketAddress(host, port));
        api.start();
    }

    /**
     * Stop the {@link GunshotAPI} connection.
     */
    public static void stop() {
        if (api == null) throw new IllegalStateException("The API does not exist");

        try {
            api.stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Send a gunshot register on broadcast.
     *
     * @param frequency: the extracted {@link Frequency}.
     */
    public static void sendFrequencies(final Frequency frequency) {
        api.sendFrequencies(frequency.deviceId, frequency.values);
    }

    /**
     * Send a gunshot register on broadcast.
     *
     * @param gunshot: the registered {@link Gunshot}.
     */
    public static void sendGunshot(final Gunshot gunshot) {
        api.sendGunshot(gunshot.deviceId, gunshot.timestamp);
    }

}
