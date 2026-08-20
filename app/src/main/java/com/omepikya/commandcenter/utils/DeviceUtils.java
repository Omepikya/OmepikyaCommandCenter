package com.omepikya.commandcenter.utils;

import android.os.Build;

import com.omepikya.commandcenter.models.DeviceInfo;

public final class DeviceUtils {

    private DeviceUtils() {
        // Prevent instantiation.
    }

    public static DeviceInfo getDeviceInfo() {

        return new DeviceInfo(
                Build.MANUFACTURER,
                Build.MODEL,
                Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT
        );
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getModel() {
        return Build.MODEL;
    }

    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }
}