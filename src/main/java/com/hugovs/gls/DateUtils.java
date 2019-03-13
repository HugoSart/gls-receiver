package com.hugovs.gls;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private DateUtils() {
        //no instance
    }

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    public static long timestamp() {
        return new Date().getTime();
    }

}
