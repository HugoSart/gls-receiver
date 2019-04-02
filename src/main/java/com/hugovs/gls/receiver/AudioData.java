package com.hugovs.gls.receiver;

import com.hugovs.gls.util.ByteUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains all the data and metadata of the audio received on the server.
 *
 * @author Hugo Sartori
 */
public class AudioData {

    private final long sourceId;
    private final long timestamp;
    private final byte[] samples;
    private final Map<String, Object> properties = new HashMap<>();

    AudioData(long sourceId, long timestamp, byte[] data) {
        this.sourceId = sourceId;
        this.timestamp = timestamp;
        this.samples = data;
    }

    public long getSourceId() {
        return sourceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getSamples() {
        return samples;
    }

    public Object getProperty(String property) {
        return properties.get(property);
    }

    public Object putProperty(String property, Object value) {
        return properties.put(property, value);
    }

    public boolean hasProperty(String property) {
        return properties.containsKey(property);
    }

    /**
     * Create a new instance of {@link AudioData} from a byte array.
     * The first 8 bytes are the device's id, the next 8 bytes are the timestamp and the remaining ones
     * are the audio samples.
     *
     * @param data a {@code byte} array to be converted to an instance of {@link AudioData}.
     * @return an instance of {@link AudioData}.
     */
    public static AudioData wrap(byte[] data) {
        return new AudioData(
                ByteUtils.bytesToLong(Arrays.copyOfRange(data, 0, 8)),
                ByteUtils.bytesToLong(Arrays.copyOfRange(data, 8, 16)),
                Arrays.copyOfRange(data, 16, data.length)
        );
    }

}
