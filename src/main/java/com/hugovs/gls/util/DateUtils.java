package com.hugovs.gls.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date utilities.
 */
public class DateUtils {

    private DateUtils() {
        //no instance
    }

    /**
     * The default data format definition of this application.
     */
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    /**
     * Get the current system timestamp.
     * @return the current system timestamp.
     */
    public static long timestamp() {
        return new Date().getTime();
    }

}
