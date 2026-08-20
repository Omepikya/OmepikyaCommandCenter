package com.omepikya.commandcenter.device;

import android.media.AudioManager;
import android.content.Context;

public final class DeviceCommandHandler {

    private final DeviceIntelligence device;
    private final AudioManager audio;

    public DeviceCommandHandler(Context context) {
        device = new DeviceIntelligence(context);

        audio = (AudioManager)
                device.getContext()
                        .getSystemService(Context.AUDIO_SERVICE);
    }

    public boolean setMusicVolume(int percent) {

        if (audio == null) {
            return false;
        }

        int max =
                audio.getStreamMaxVolume(
                        AudioManager.STREAM_MUSIC);

        int v =
                Math.max(0, Math.min(100, percent))
                        * max / 100;

        audio.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                v,
                0);

        return true;
    }

    public boolean openSettingFor(DeviceCapability c) {

        if (c == null) {
            return false;
        }

        switch (c) {

            case WIFI:
                device.openWifiSettings();
                return true;

            case BLUETOOTH:
                device.openBluetoothSettings();
                return true;

            case NETWORK:
                device.openNetworkSettings();
                return true;

            case BRIGHTNESS:
                device.openDisplaySettings();
                return true;

            case VOLUME:
                device.openSoundSettings();
                return true;

            default:
                return false;
        }
    }

    public DeviceIntelligence getDeviceIntelligence() {
        return device;
    }
}