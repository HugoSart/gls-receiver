package com.hugovs.gls.util;

import java.nio.ByteBuffer;

/**
 * Byte utilities.
 *
 * @author Hugo Sartori
 */
public class ByteUtils {

    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    /**
     * Converts a {@code long} into a {@code byte} array.
     *
     * @param x the number to be converted to bytes.
     * @return the {@code byte} array.
     */
    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    /**
     * Converts a {@code byte} array to a {@code long}.
     *
     * @param bytes the {@code byte} array to be converted.
     * @return the {@code long} value.
     */
    public static long bytesToLong(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return bb.getLong();
    }
}
