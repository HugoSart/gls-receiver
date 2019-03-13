package com.hugovs.gls;

public class StringUtils {

    private StringUtils() {
        //no instance
    }

    public static String from(byte[] buffer) {
        StringBuilder prt = new StringBuilder("[");
        int i = 0;
        for (byte b : buffer) {
            prt.append(getHex(new byte[]{b}));
            if (i < buffer.length - 1)
                prt.append(" ");
            i++;
        }
        prt.append("]");
        return prt.toString();
    }

    static final String HEXES = "0123456789ABCDEF";
    private static String getHex( byte [] raw ) {
        if ( raw == null ) {
            return null;
        }
        final StringBuilder hex = new StringBuilder( 2 * raw.length );
        for ( final byte b : raw ) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

}
