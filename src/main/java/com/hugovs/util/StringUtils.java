package com.hugovs.util;

import java.util.Collection;

/**
 * String utilities.
 *
 * @author Hugo Sartori
 */
public class StringUtils {

    private StringUtils() {
        //no instance
    }

    /**
     * The hexadecimal representation of each number in a {@link String} format.
     */
    private static final String HEXES = "0123456789ABCDEF";

    /**
     * Join a {@link Collection} into a {@link String} with each element separated with commas.
     *
     * @param collection: the {@link Collection} to be joined into a {@link String}.
     * @return the {@link String} of the joined elements.
     */
    public static String join(Collection<?> collection) {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (Object o : collection) {
            builder.append(o.toString());
            if (count < collection.size() - 1) builder.append(", ");
            count++;
        }
        return builder.toString();
    }

    /**
     * Generate a {@link String} of a byte array, separated with spaces.
     *
     * @param bytes the byte array to be converted to an hexadecimal {@link String}.
     * @param hex if the {@link String} needs to be built using hexadecimal representation of bytes.
     * @return an hexadecimal {@link String} separated with spaces.
     */
    public static String from(byte[] bytes, boolean hex) {
        StringBuilder prt = new StringBuilder("[");
        int i = 0;
        for (byte b : bytes) {
            if (hex) prt.append(getHex(new byte[]{b}));
            else prt.append(String.format("%4d", b));
            if (i < bytes.length - 1)
                prt.append(" ");
            i++;
        }
        prt.append("]");
        return prt.toString();
    }

    /**
     * Generate a hexadecimal {@link String} of a byte array, separated with spaces.
     *
     * @param bytes the byte array to be converted to an hexadecimal {@link String}.
     * @return an hexadecimal {@link String} separated with spaces.
     */
    public static String from(byte[] bytes) {
        return from(bytes, true);
    }

    /**
     * Get the hexadecimal {@link String} representation of a byte array.
     *
     * @param bytes the byte array to be converted to an single hexadecimal {@link String}.
     * @return the hexadecimal representation in {@link String} of the {@code bytes}.
     */
    private static String getHex(byte[] bytes ) {
        if ( bytes == null ) {
            return null;
        }
        final StringBuilder hex = new StringBuilder( 2 * bytes.length );
        for ( final byte b : bytes ) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

}
