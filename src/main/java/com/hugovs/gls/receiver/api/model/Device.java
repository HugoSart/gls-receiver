package com.hugovs.gls.receiver.api.model;

import java.io.Serializable;

/**
 * Represents a device that is used to listen to gunshot remotely.
 *
 * @author Hugo Sartori
 */
public class Device implements Serializable {
    public long id;
    public double latitude, longitude;

    public Device(long id, double latitude, double longitude) {
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
