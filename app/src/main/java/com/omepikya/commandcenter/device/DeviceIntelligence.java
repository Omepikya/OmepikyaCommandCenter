package com.omepikya.commandcenter.device;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.provider.Settings;
import android.media.AudioManager;

import java.util.EnumSet;
import java.util.Set;

public final class DeviceIntelligence {

    private final Context context;
    private final AudioManager audio;

    public DeviceIntelligence(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        this.context = context.getApplicationContext();
        this.audio = (AudioManager)
                this.context.getSystemService(Context.AUDIO_SERVICE);
    }

    public Set<DeviceCapability> getCapabilities() {
        EnumSet<DeviceCapability> s =
                EnumSet.allOf(DeviceCapability.class);

        if (!context.getPackageManager()
                .hasSystemFeature("android.hardware.camera")) {
            s.remove(DeviceCapability.SCREEN);
        }

        return s;
    }

    public DeviceState snapshot() {

        BatteryManager bm =
                (BatteryManager) context.getSystemService(
                        Context.BATTERY_SERVICE);

        int battery = bm == null
                ? -1
                : bm.getIntProperty(
                        BatteryManager.BATTERY_PROPERTY_CAPACITY);

        boolean charging = false;

        android.content.Intent i =
                context.registerReceiver(
                        null,
                        new android.content.IntentFilter(
                                Intent.ACTION_BATTERY_CHANGED));

        if (i != null) {
            int st = i.getIntExtra(
                    BatteryManager.EXTRA_STATUS, -1);

            charging =
                    st == BatteryManager.BATTERY_STATUS_CHARGING
                    || st == BatteryManager.BATTERY_STATUS_FULL;
        }

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni =
                cm == null ? null : cm.getActiveNetworkInfo();

        boolean network =
                ni != null && ni.isConnected();

        boolean airplane =
                Settings.Global.getInt(
                        context.getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON,
                        0) != 0;

        int music =
                audio == null
                        ? 0
                        : audio.getStreamVolume(
                                AudioManager.STREAM_MUSIC);

        int alarm =
                audio == null
                        ? 0
                        : audio.getStreamVolume(
                                AudioManager.STREAM_ALARM);

        int ring =
                audio == null
                        ? 0
                        : audio.getStreamVolume(
                                AudioManager.STREAM_RING);

        int brightness;

        try {
            brightness =
                    Settings.System.getInt(
                            context.getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            brightness = -1;
        }

        return new DeviceState(
                battery,
                charging,
                network,
                isBluetoothEnabled(),
                airplane,
                music,
                alarm,
                ring,
                brightness,
                isScreenInteractive()
        );
    }

    public boolean isBluetoothEnabled() {
        try {
            android.bluetooth.BluetoothAdapter a =
                    android.bluetooth.BluetoothAdapter
                            .getDefaultAdapter();

            return a != null && a.isEnabled();

        } catch (Exception e) {
            return false;
        }
    }

    public boolean isScreenInteractive() {
        android.os.PowerManager p =
                (android.os.PowerManager)
                        context.getSystemService(
                                Context.POWER_SERVICE);

        return p != null && p.isInteractive();
    }

    public void openWifiSettings() {
        open(Settings.ACTION_WIFI_SETTINGS);
    }

    public void openBluetoothSettings() {
        open(Settings.ACTION_BLUETOOTH_SETTINGS);
    }

    public void openNetworkSettings() {
        open(Settings.ACTION_WIRELESS_SETTINGS);
    }

    public void openDisplaySettings() {
        open(Settings.ACTION_DISPLAY_SETTINGS);
    }

    public void openSoundSettings() {
        open(Settings.ACTION_SOUND_SETTINGS);
    }

    public void open(String action) {
        try {
            Intent i = new Intent(action);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception ignored) {
        }
    }

    public Context getContext() {
        return context;
    }
}