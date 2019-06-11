package com.hugovs.gls.receiver.api.model;

import java.io.Serializable;

/**
 * Represents a gunshot detection by a device.
 *
 * @author Hugo Sartori
 */
public class Gunshot implements Serializable {
    public long deviceId;
    public long timestamp;

    public Gunshot(long deviceId, long timestamp) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
    }
}
