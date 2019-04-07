package com.hugovs.gls.receiver;

import com.hugovs.util.ByteUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains all the data and metadata of the audio received on the server.
 *
 * @author Hugo Sartori
 */
public class AudioData {

    /**
     * Extra properties of the audio.
     * This can be used, for example, to calculate audio metadata and pass it thought for the next extension.
     */
    private final Map<String, Object> properties = new HashMap<>();

    /**
     * The id of the audio source, the one who generated this data.
     */
    private final long sourceId;

    /**
     * The time when this data was generated (if calculated internally, needs to be calculated as soon as possible).
     */
    private final long timestamp;

    /**
     * The samples of the audio.
     */
    private final byte[] samples;

    /**
     * Create an instance of this class.
     *
     * @param sourceId: the id of the one who generated this data.
     * @param timestamp: the time when this data was generated.
     * @param data: the audio samples.
     */
    AudioData(long sourceId, long timestamp, byte[] data) {
        this.sourceId = sourceId;
        this.timestamp = timestamp;
        this.samples = data;
    }

    /**
     * Get the data source id.
     *
     * @return the source id.
     */
    public long getSourceId() {
        return sourceId;
    }

    /**
     * Get the data timestamp.
     *
     * @return: the data timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Get the data samples.
     *
     * @return: an {@code byte} array holding each sample in order.
     */
    public byte[] getSamples() {
        return samples;
    }

    /**
     * Get an extra audio property.
     *
     * @param property: the property name.
     * @return {@link Object} : an object holding the value of the property;
     *         {@code null}   : if the property does not exist in this data.
     */
    public Object getProperty(String property) {
        return properties.get(property);
    }

    /**
     * Add a property and its value to this data.
     *
     * @param property: the name of the property.
     * @param value: the value of the property.
     * @return the value of the property.
     */
    public Object putProperty(String property, Object value) {
        return properties.put(property, value);
    }

    /**
     * Checks if the data has a property.
     *
     * @param property: the name of the property.
     * @return {@link Object} : the value of the property;
     *         {@code null}   : if the property does not exist on this data.
     */
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
