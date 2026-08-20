package com.omepikya.commandcenter.models;

public class DeviceInfo {

    private final String manufacturer;
    private final String model;
    private final String androidVersion;
    private final int sdkVersion;

    public DeviceInfo(
            String manufacturer,
            String model,
            String androidVersion,
            int sdkVersion
    ) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.androidVersion = androidVersion;
        this.sdkVersion = sdkVersion;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public int getSdkVersion() {
        return sdkVersion;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", androidVersion='" + androidVersion + '\'' +
                ", sdkVersion=" + sdkVersion +
                '}';
    }
}