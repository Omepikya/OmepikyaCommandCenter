package com.omepikya.commandcenter.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

    private DateUtils() {
        // Prevent instantiation.
    }

    private static final String DATE_TIME_FORMAT =
            "dd MMM yyyy, HH:mm:ss";

    public static String getCurrentDateTime() {

        SimpleDateFormat formatter =
                new SimpleDateFormat(
                        DATE_TIME_FORMAT,
                        Locale.getDefault()
                );

        return formatter.format(new Date());
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
}