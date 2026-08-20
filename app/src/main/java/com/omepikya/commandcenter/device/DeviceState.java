package com.omepikya.commandcenter.device;

public final class DeviceState {
    private final int batteryPercent;
    private final boolean charging;
    private final boolean wifiEnabled;
    private final boolean bluetoothEnabled;
    private final boolean airplaneMode;
    private final int musicVolume;
    private final int alarmVolume;
    private final int ringVolume;
    private final int brightness;
    private final boolean screenInteractive;

    public DeviceState(int batteryPercent, boolean charging, boolean wifiEnabled,
                       boolean bluetoothEnabled, boolean airplaneMode,
                       int musicVolume, int alarmVolume, int ringVolume,
                       int brightness, boolean screenInteractive) {
        this.batteryPercent = batteryPercent;
        this.charging = charging;
        this.wifiEnabled = wifiEnabled;
        this.bluetoothEnabled = bluetoothEnabled;
        this.airplaneMode = airplaneMode;
        this.musicVolume = musicVolume;
        this.alarmVolume = alarmVolume;
        this.ringVolume = ringVolume;
        this.brightness = brightness;
        this.screenInteractive = screenInteractive;
    }

    public int getBatteryPercent() {
        return batteryPercent;
    }

    public boolean isCharging() {
        return charging;
    }

    public boolean isWifiEnabled() {
        return wifiEnabled;
    }

    public boolean isBluetoothEnabled() {
        return bluetoothEnabled;
    }

    public boolean isAirplaneMode() {
        return airplaneMode;
    }

    public int getMusicVolume() {
        return musicVolume;
    }

    public int getAlarmVolume() {
        return alarmVolume;
    }

    public int getRingVolume() {
        return ringVolume;
    }

    public int getBrightness() {
        return brightness;
    }

    public boolean isScreenInteractive() {
        return screenInteractive;
    }
}