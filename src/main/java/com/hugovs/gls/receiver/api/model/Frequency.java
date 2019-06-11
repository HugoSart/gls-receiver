package com.hugovs.gls.receiver.api.model;

import java.io.Serializable;

/**
 * Represents a frequency spectrum extracted from a device.
 *
 * @author Hugo Sartori
 */
public class Frequency implements Serializable {
    public long deviceId;
    public double[] values;

    public Frequency(long deviceId, double[] values) {
        this.deviceId = deviceId;
        this.values = values;
    }
}
