package com.hugovs.gls;

/**
 * String utilities.
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
     * Generate a hexadecimal {@link String} of a byte array, separated with spaces.
     *
     * @param bytes the byte array to be converted to an hexadecimal {@link String}.
     * @return an hexadecimal {@link String} separated with spaces.
     */
    public static String from(byte[] bytes) {
        StringBuilder prt = new StringBuilder("[");
        int i = 0;
        for (byte b : bytes) {
            prt.append(getHex(new byte[]{b}));
            if (i < bytes.length - 1)
                prt.append(" ");
            i++;
        }
        prt.append("]");
        return prt.toString();
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
